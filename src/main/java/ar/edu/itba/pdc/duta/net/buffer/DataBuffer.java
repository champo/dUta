package ar.edu.itba.pdc.duta.net.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import net.jcip.annotations.GuardedBy;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.net.buffer.internal.DynamicDataBuffer;
import ar.edu.itba.pdc.duta.net.buffer.internal.FileDataBuffer;
import ar.edu.itba.pdc.duta.net.buffer.internal.InternalDataBuffer;

public class DataBuffer {

	private static final Logger logger = Logger.getLogger(DataBuffer.class);

	private InternalDataBuffer buffer;

	private int references = 1;

	private int maxDynamicDataBufferSize = 0x1400000; // 20 MB...
	
	private boolean keepBuffer = true;
	
	private Object lock = new Object();


	public DataBuffer() {

		buffer = new DynamicDataBuffer();
	}

	public DataBuffer(int capacity) {

		buffer = new DynamicDataBuffer(capacity);
	}

	public DataBuffer(ByteBuffer buffer) {

		this.buffer = new DynamicDataBuffer(buffer);
	}

	public DataBuffer(byte[] buffer) {

		this(ByteBuffer.wrap(buffer));
	}


	public void doNotKeepBuffer() {

		keepBuffer = false;
	}


	// Doesn't actually read, just sets the input channel
	// To read, use get or consume
	public void readFrom(ReadableByteChannel channel) {

		synchronized (lock) {
			buffer.setInputChannel(channel);
		}
	}

	// Writes everything (from ReadIndex) to the given output channel
	public void writeTo(WritableByteChannel channel) throws IOException {

		synchronized (lock) {
			buffer.setOutputChannel(channel);
			buffer.write();

			if (!keepBuffer && !hasReadableBytes()) {

				buffer.setReadIndex(0);
				buffer.setWriteIndex(0);
			}
		}
	}


	public int getReadIndex() {

		synchronized (lock) {
			return buffer.getReadIndex();
		}
	}

	public void setReadIndex(int index) {

		synchronized (lock) {
			buffer.setReadIndex(index);
		}
	}

	public int getWriteIndex() {

		synchronized (lock) {
			return buffer.getWriteIndex();
		}
	}

	public void setWriteIndex(int index) {

		synchronized (lock) {
			buffer.setWriteIndex(index);
		}
	}


	public boolean hasReadableBytes() {

		synchronized (lock) {
			return buffer.getWriteIndex() > buffer.getReadIndex();
		}
	}

	public int remainingBytes() {

		synchronized (lock) {
			return buffer.getWriteIndex() - buffer.getReadIndex();
		}
	}


	@GuardedBy("lock")
	private void checkSize(int newAdd) throws IOException {

		if (!keepBuffer) {
			return;
		}
		if (maxDynamicDataBufferSize < 0) {
			return;
		}
		if (buffer.getWriteIndex() + newAdd < maxDynamicDataBufferSize) {
			return;
		}

		maxDynamicDataBufferSize = -1;

		InternalDataBuffer aux = new FileDataBuffer((DynamicDataBuffer)buffer);
		buffer.collect();
		buffer = aux;
		
	}

	public byte[] read() throws IOException {
		
		synchronized (lock) {
			byte[] bytes = new byte[remainingBytes()];
			buffer.get(buffer.getReadIndex(), bytes, 0, bytes.length);
			return bytes;
		}
	}

	public byte get() throws IOException {

		synchronized (lock) {
			checkSize(1);
	
			byte[] ret = new byte[1];
			int oldWriteIndex = buffer.getWriteIndex();
	
			buffer.read(1);
			buffer.get(oldWriteIndex, ret, 0, 1);
	
			return ret[0];
		}
	}


	public void consume(int count) throws IOException {

		synchronized (lock) {
			checkSize(count);
			buffer.read(count);
		}
	}

	public void consume() throws IOException {

		synchronized (lock) {
			checkSize(1);
			buffer.read(1);
		}
	}


	// Reference counting

	public void retain() {

		synchronized (lock) {
			references++;
		}
	}

	public void release() {

		synchronized (lock) {
			if (--references == 0) {
				buffer.collect();
			}
		}
		
	}

	@Override
	protected void finalize() throws Throwable {

		if (references != 0) {
			logger.fatal(this + ": I was GC'd with a reference count of " + references);
			buffer.collect();
		}

		super.finalize();
	}
}
