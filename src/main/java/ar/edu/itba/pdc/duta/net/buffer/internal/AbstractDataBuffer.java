package ar.edu.itba.pdc.duta.net.buffer.internal;

import java.nio.channels.WritableByteChannel;

import ar.edu.itba.pdc.duta.net.BufferedReadableByteChannel;

public abstract class AbstractDataBuffer implements InternalDataBuffer {

	protected int readIndex = 0;

	protected int writeIndex = 0;

	protected BufferedReadableByteChannel inputChannel;
	
	protected WritableByteChannel outputChannel;


	@Override
	public void setInputChannel(BufferedReadableByteChannel channel) {

		inputChannel = channel;
	}

	@Override
	public void setOutputChannel(WritableByteChannel channel) {

		outputChannel = channel;
	}

	@Override
	public BufferedReadableByteChannel getInputChannel() {

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

}
