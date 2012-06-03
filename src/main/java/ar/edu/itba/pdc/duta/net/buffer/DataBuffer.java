package ar.edu.itba.pdc.duta.net.buffer;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public interface DataBuffer {
	
	public int readFrom(ReadableByteChannel channel) throws IOException;
	
	public int readFrom(ReadableByteChannel channel, int limit) throws IOException;
	
	public int writeTo(WritableByteChannel channel) throws IOException;
	
	public int writeTo(WritableByteChannel channel, int limit) throws IOException;
	
	public byte get() throws IOException;
	
	public boolean hasFreeSpace();
	
	public int getReadIndex();
	
	public void setReadIndex(int index);
	
	public int getWriteIndex();
	
	public void setWriteIndex(int index);
	
	public boolean hasReadableBytes();

	public int remaining();

	/**
	 * Read bytes from the buffer into a byte array.
	 * 
	 * This attemps to read count bytes from the buffer into bytes, starting at offset on bytes.
	 * The bytes read start from the current readIndex of the buffer. 
	 * 
	 * @param bytes
	 * @param offset
	 * @param count
	 * @throws IOException
	 */
	public void get(byte[] bytes, int offset, int count) throws IOException;
	
	public void retain();
	
	public void release();
}
