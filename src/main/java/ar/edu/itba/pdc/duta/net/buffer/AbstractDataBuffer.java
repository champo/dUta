package ar.edu.itba.pdc.duta.net.buffer;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public abstract class AbstractDataBuffer implements DataBuffer {

	protected static final int READ_SIZE = (int) Math.pow(2, 20);
	
	protected int writeIndex = 0;
	
	protected int readIndex = 0;

	public AbstractDataBuffer() {
		super();
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

}