package ar.edu.itba.pdc.duta.net.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class FixedDataBuffer extends AbstractDataBuffer {
	
	private ByteBuffer buffer;
	
	private int writeIndex = 0;
	
	private int readIndex = 0;
	
	public FixedDataBuffer(int capacity) {
		this(ByteBuffer.allocate(capacity));
	}
	
	public FixedDataBuffer(ByteBuffer buffer) {
		super();
		this.buffer = buffer;
	}
	
	public FixedDataBuffer(byte[] data) {
		this(ByteBuffer.wrap(data));
	}
	

	@Override
	public int readFrom(ReadableByteChannel channel, int limit) throws IOException {
		
		buffer.limit(Math.min(writeIndex, readIndex + limit));
		buffer.position(readIndex);
		
		int read = channel.read(buffer);
		readIndex = buffer.position();
		
		return read;
	}

	@Override
	public int readFrom(ReadableByteChannel channel) throws IOException {
		
		buffer.limit(writeIndex);
		buffer.position(readIndex);
		
		int read = channel.read(buffer);
		readIndex = buffer.position();
		
		return read;
	}
	
	@Override
	public int writeTo(WritableByteChannel channel, int limit) throws IOException {

		buffer.limit(Math.min(buffer.capacity(), writeIndex + limit));
		buffer.position(writeIndex);
		
		int res = channel.write(buffer);
		writeIndex = buffer.position();
		
		return res;

	}

	@Override
	public int writeTo(WritableByteChannel channel) throws IOException {

		buffer.limit(buffer.capacity());
		buffer.position(writeIndex);
		
		int res = channel.write(buffer);
		writeIndex = buffer.position();
		
		return res;
	}

	@Override
	public byte get() {
		
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
	public void collect() {
		buffer = null;
	}

	@Override
	public void get(byte[] bytes, int offset, int count) throws IOException {
		buffer.limit(writeIndex);
		buffer.get(bytes, offset, count);
	}

}
