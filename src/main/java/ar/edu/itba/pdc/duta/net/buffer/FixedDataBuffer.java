package ar.edu.itba.pdc.duta.net.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class FixedDataBuffer implements DataBuffer {
	
	private ByteBuffer buffer;
	
	private int writeIndex = 0;
	
	private int readIndex = 0;
	
	public FixedDataBuffer(int capacity) {
		super();
		this.buffer = ByteBuffer.allocate(capacity);
	}
	
	public FixedDataBuffer(ByteBuffer buffer) {
		super();
		this.buffer = buffer;
	}

	@Override
	public int readFrom(ReadableByteChannel channel) throws IOException {
		
		buffer.position(readIndex);
		int read = channel.read(buffer);
		readIndex = buffer.position();
		
		return read;
	}

	@Override
	public int writeTo(WritableByteChannel channel) throws IOException {

		buffer.position(writeIndex);
		int res = channel.write(buffer);
		writeIndex = buffer.position();
		
		return res;
	}

	@Override
	public byte getByte() {
		
		buffer.position(readIndex);
		byte res = buffer.get();
		readIndex = buffer.position();
		
		return res;
	}

	@Override
	public boolean hasFreeSpace() {
		return buffer.capacity() >= writeIndex;
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
	public void collect() {
		buffer = null;
	}

}
