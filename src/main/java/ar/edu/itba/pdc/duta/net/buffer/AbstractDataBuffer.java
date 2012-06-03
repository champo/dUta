package ar.edu.itba.pdc.duta.net.buffer;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.apache.log4j.Logger;

public abstract class AbstractDataBuffer implements DataBuffer {
	
	private static final Logger logger = Logger.getLogger(AbstractDataBuffer.class);

	protected static final int READ_SIZE = (int) Math.pow(2, 20);
	
	protected int writeIndex = 0;
	
	protected int readIndex = 0;
	
	protected int references;

	public AbstractDataBuffer() {
		super();
		references = 1;
	}

	@Override
	public int readFrom(ReadableByteChannel channel) throws IOException {
		
		int total = 0, bytes;
		
		while ((bytes = readFrom(channel, READ_SIZE)) != 0) {
			total += bytes;
		}
		
		return total;
	}

	@Override
	public int writeTo(WritableByteChannel channel) throws IOException {
	
		long total = 0L, bytes;
		
		while ((bytes = writeTo(channel, READ_SIZE)) != 0) {
			total += bytes;
		}
		
		return (int) total;
	}

	@Override
	public int getReadIndex() {
		return readIndex;
	}

	@Override
	public void setReadIndex(int index) {
		readIndex = index;
	}

	@Override
	public int getWriteIndex() {
		return writeIndex;
	}

	@Override
	public void setWriteIndex(int index) {
		writeIndex = index;
	}

	@Override
	public boolean hasReadableBytes() {
		return writeIndex > readIndex;
	}

	@Override
	public int remaining() {
		return writeIndex - readIndex;
	}
	
	protected abstract void collect();

	@Override
	public void release() {
		references--;
		
		if (references == 0) {
			collect();
		}
	}
	
	@Override
	public void retain() {
		references++;
	}
	
	@Override
	protected void finalize() throws Throwable {
		
		if (references != 0) {
			logger.fatal(this + ": I was GC'd with a reference count of " + references);
			collect();
		}
		
		super.finalize();
	}
}