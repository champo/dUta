package ar.edu.itba.pdc.duta.net.buffer.internal;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public abstract class AbstractDataBuffer implements InternalDataBuffer {

	protected int readIndex = 0;

	protected int writeIndex = 0;

	protected ReadableByteChannel inputChannel;
	
	protected WritableByteChannel outputChannel;


	@Override
	public void setInputChannel(ReadableByteChannel channel) {

		inputChannel = channel;
	}

	@Override
	public void setOutputChannel(WritableByteChannel channel) {

		outputChannel = channel;
	}

	@Override
	public ReadableByteChannel getInputChannel() {

		return inputChannel;
	}

	@Override
	public WritableByteChannel getOutputChannel() {

		return outputChannel;
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
	public abstract void read(int count) throws IOException;

	@Override
	public abstract void get(int pos, byte[] buffer, int offset, int count) throws IOException;

	@Override
	public abstract void write() throws IOException;

	@Override
	public abstract void collect();

}
