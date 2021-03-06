package ar.edu.itba.pdc.duta.net.buffer.internal;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public interface InternalDataBuffer {

	public void setInputChannel(ReadableByteChannel channel);

	public void setOutputChannel(WritableByteChannel channel);

	public ReadableByteChannel getInputChannel();

	public WritableByteChannel getOutputChannel();


	public int getReadIndex();

	public void setReadIndex(int index);

	public int getWriteIndex();

	public void setWriteIndex(int index);


	public void read(int count) throws IOException;

	public void write() throws IOException;


	public void get(int pos, byte[] buffer, int offset, int count) throws IOException;


	public void collect();
}
