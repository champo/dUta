package ar.edu.itba.pdc.duta.net.buffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.apache.log4j.Logger;

public class FileDataBuffer implements DataBuffer {
	
	private static final Logger logger = Logger.getLogger(FileDataBuffer.class);
	
	private static final int READ_SIZE = (int) Math.pow(2, 10);
	
	private RandomAccessFile file;
	
	private FileChannel fileChannel;
	
	private int writeIndex = 0;
	
	private int readIndex = 0;
	
	private MappedByteBuffer buffer = null;
	
	private int start = 0;
	
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
	
	@Override
	public int readFrom(ReadableByteChannel channel) throws IOException {
		
		long total = 0L, bytes;
		
		while ((bytes = fileChannel.transferFrom(channel, writeIndex, READ_SIZE)) != 0) {
			total += bytes;
			writeIndex += bytes;
		}
		
		return (int) total;
	}

	@Override
	public int writeTo(WritableByteChannel channel) throws IOException {

		long total = 0L, bytes;
		
		while ((bytes = fileChannel.transferTo(READ_SIZE, readIndex, channel)) != 0) {
			total += bytes;
			readIndex += bytes;
		}
		
		
		return (int) total;
	}

	@Override
	public byte getByte() throws IOException {
		
		if (buffer == null) {
			
			try {
				buffer = fileChannel.map(MapMode.READ_ONLY, start, READ_SIZE);
			} catch (IOException e) {
				logger.error("Failed to allocate a mapped buffer", e);
				throw e;
			}
			
		} else if (start < readIndex || readIndex >= start + buffer.capacity()) {
			
			int offset = readIndex % READ_SIZE;
			
			if (offset > writeIndex) {
				throw new BufferUnderflowException();
			}
			
			try {
				buffer = fileChannel.map(MapMode.READ_ONLY, offset, READ_SIZE);
				start = offset;
			} catch (IOException e) {
				logger.error("Failed to allocate a mapped buffer", e);
				throw e;
			}
			
		}
		
		buffer.position(readIndex - start);
		byte res = buffer.get();
		readIndex++;
		
		return res;
	}

	@Override
	public boolean hasFreeSpace() {
		return true;
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
	public void collect() {
		
		buffer = null;
		
		try {
			file.close();
		} catch (IOException e) {
			logger.warn("Failed to close a file", e);
		}
		
		file = null;
	}

}
