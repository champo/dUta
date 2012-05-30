package ar.edu.itba.pdc.duta.net.buffer;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class WrappedDataBuffer implements DataBuffer {
	
	private DataBuffer backer;
	
	private int offset;
	
	private int length;

	/**
	 * Wrap a DataBuffer in order to show only a slice of the original.
	 * 
	 * This class assumes it's the only one using backer. If it's manipulated from the outside,
	 * ugly things may happen.
	 * 
	 * @param backer
	 * @param offset
	 * @param length
	 */
	public WrappedDataBuffer(DataBuffer backer, int offset, int length) {
		super();
		this.backer = backer;
		this.offset = offset;
		this.length = length;
	}

	@Override
	public int readFrom(ReadableByteChannel channel) throws IOException {
		return backer.readFrom(channel, offset + length - backer.getReadIndex());
	}

	@Override
	public int readFrom(ReadableByteChannel channel, int limit) throws IOException {
		return backer.readFrom(channel, Math.min(limit, offset + length - backer.getReadIndex()));
	}

	@Override
	public int writeTo(WritableByteChannel channel) throws IOException {
		return backer.writeTo(channel, offset + length - backer.getWriteIndex());
	}

	@Override
	public int writeTo(WritableByteChannel channel, int limit) throws IOException {
		return backer.writeTo(channel, Math.min(limit, offset + length - backer.getWriteIndex()));
	}

	@Override
	public byte get() throws IOException {
		return backer.get();
	}

	@Override
	public boolean hasFreeSpace() {
		return offset + length >= backer.getWriteIndex();
	}

	@Override
	public int getReadIndex() {
		return backer.getReadIndex() - offset;
	}

	@Override
	public void setReadIndex(int index) {
		backer.setReadIndex(index + offset);
	}

	@Override
	public int getWriteIndex() {
		return backer.getWriteIndex() - offset;
	}

	@Override
	public void setWriteIndex(int index) {
		backer.setWriteIndex(index + offset);
	}

	@Override
	public void collect() {
		backer.collect();
		backer = null;
	}

	@Override
	public boolean hasReadableBytes() {
		return remaining() > 0;
	}

	@Override
	public int remaining() {
		return getWriteIndex() - getReadIndex();
	}

	@Override
	public void get(byte[] bytes, int offset, int count) throws IOException {
		backer.get(bytes, offset, Math.min(count, remaining()));
	}

}
