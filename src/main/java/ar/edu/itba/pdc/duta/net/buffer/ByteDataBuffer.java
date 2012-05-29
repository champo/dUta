package ar.edu.itba.pdc.duta.net.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class ByteDataBuffer implements DataBuffer {
	
	private ByteBuffer buffer;
	
	private int writePos = 0;
	
	public ByteDataBuffer(ByteBuffer buffer) {
		super();
		this.buffer = buffer;
	}

	@Override
	public int readFrom(ReadableByteChannel channel) throws IOException {
		int old = buffer.position();
		int read = channel.read(buffer);
		buffer.position(old);
		
		return read;
	}

	@Override
	public int writeTo(WritableByteChannel channel) throws IOException {
		int old = buffer.position();
		buffer.position(writePos);
		int res = channel.write(buffer);
		writePos = buffer.position();
		buffer.position(old);
		
		return res;
	}

	@Override
	public void setPosition(int position) {
		buffer.position(position);
	}

	@Override
	public int getPosition() {
		return buffer.position();
	}

	@Override
	public int getLength() {
		return buffer.limit();
	}

	@Override
	public byte getByte() {
		return buffer.get();
	}

	@Override
	public boolean hasFreeSpace() {
		return buffer.hasRemaining();
	}

}
