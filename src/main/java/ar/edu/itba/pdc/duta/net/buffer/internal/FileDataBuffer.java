package ar.edu.itba.pdc.duta.net.buffer.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;


public class FileDataBuffer extends AbstractDataBuffer {

	private static final Logger logger = Logger.getLogger(FileDataBuffer.class);

	private RandomAccessFile file;

	private FileChannel fileChannel;


	public FileDataBuffer() throws IOException {

		try {

			File tmp = File.createTempFile("duta", null);
			file = new RandomAccessFile(tmp, "rw");
			tmp.delete();

		} catch (FileNotFoundException e) {

			throw new IOException(e);
		}

		fileChannel = file.getChannel();
	}

	public FileDataBuffer(DynamicDataBuffer dynamicDataBuffer) throws IOException {

		this();

		dynamicDataBuffer.writeToFile(fileChannel);

		readIndex = dynamicDataBuffer.getReadIndex();
		writeIndex = dynamicDataBuffer.getWriteIndex();
		inputChannel = dynamicDataBuffer.getInputChannel();
		outputChannel = dynamicDataBuffer.getOutputChannel();
	}

	@Override
	public void read(int count) throws IOException {

		writeIndex += fileChannel.transferFrom(inputChannel, writeIndex, count);
	}

	@Override
	public void write() throws IOException {

		readIndex += fileChannel.transferTo(readIndex, writeIndex - readIndex, outputChannel);
	}

	@Override
	public void get(int pos, byte[] buffer, int offset, int count) throws IOException {

		fileChannel.read(ByteBuffer.wrap(buffer, offset, count), pos);
	}

	@Override
	public void collect() {

		try {
			file.close();
		} catch (IOException e) {
			logger.warn("Failed to close a file", e);
		}

		file = null;
	}

}
