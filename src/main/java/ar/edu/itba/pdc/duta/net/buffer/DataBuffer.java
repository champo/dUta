package ar.edu.itba.pdc.duta.net.buffer;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public interface DataBuffer {
	
	public int readFrom(ReadableByteChannel channel) throws IOException;
	
	public int writeTo(WritableByteChannel channel) throws IOException;
	
	public void setPosition(int position);
	
	public int getPosition();
	
	public int getLength();
	
	public byte getByte();
	
	public boolean hasFreeSpace();

}
