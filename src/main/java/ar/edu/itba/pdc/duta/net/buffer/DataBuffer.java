package ar.edu.itba.pdc.duta.net.buffer;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public interface DataBuffer {
	
	public int readFrom(ReadableByteChannel channel) throws IOException;
	
	public int writeTo(WritableByteChannel channel) throws IOException;
	
	public byte getByte() throws IOException;
	
	public boolean hasFreeSpace();
	
	public int getReadIndex();
	
	public void setReadIndex(int index);
	
	public int getWriteIndex();
	
	public void setWriteIndex(int index);
	
	public void collect();
	
}
