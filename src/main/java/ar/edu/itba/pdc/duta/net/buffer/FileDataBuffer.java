package ar.edu.itba.pdc.duta.net.buffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.apache.log4j.Logger;

public class FileDataBuffer extends AbstractDataBuffer {
	
	private static final Logger logger = Logger.getLogger(FileDataBuffer.class);
	
	private RandomAccessFile file;
	
	private FileChannel fileChannel;
	
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
	public int readFrom(ReadableByteChannel channel, int limit) throws IOException {
		
		long bytes = fileChannel.transferFrom(channel, writeIndex, limit);
		writeIndex += bytes;
		
		return (int) bytes;
	}


	@Override
	public int writeTo(WritableByteChannel channel, int limit) throws IOException {
		
		long bytes = fileChannel.transferTo(readIndex, limit, channel);
		readIndex += bytes;
		
		return (int) bytes;
	}

	@Override
	public byte get() throws IOException {
		
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
	public void collect() {
		
		buffer = null;
		
		try {
			file.close();
		} catch (IOException e) {
			logger.warn("Failed to close a file", e);
		}
		
		file = null;
	}
	
	@Override
	public void get(byte[] bytes, int offset, int count) throws IOException {
		fileChannel.read(ByteBuffer.wrap(bytes, offset, count));
	}

}
