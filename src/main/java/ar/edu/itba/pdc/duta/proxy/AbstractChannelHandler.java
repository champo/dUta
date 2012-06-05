package ar.edu.itba.pdc.duta.proxy;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.parser.MessageParser;
import ar.edu.itba.pdc.duta.http.parser.ParseException;
import ar.edu.itba.pdc.duta.net.ChannelHandler;
import ar.edu.itba.pdc.duta.net.Reactor.ReactorKey;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

@ThreadSafe
public abstract class AbstractChannelHandler implements ChannelHandler {

	private static Logger logger = Logger.getLogger(AbstractChannelHandler.class);

	@GuardedBy("keyLock")
	protected ReactorKey key;

	private Deque<DataBuffer> outputQueue;

	protected boolean close = false;

	protected Object keyLock;

	private MessageParser parser;

	protected DataBuffer buffer;

	public AbstractChannelHandler() {
		outputQueue = new LinkedBlockingDeque<DataBuffer>();
		key = null;
		keyLock = new Object();
	}

	public synchronized void queueOutput(DataBuffer output, Operation op) {

		synchronized (keyLock) {
			
			if (close || !canWrite(op)) {
				throw new IllegalStateException();
			}
			
			synchronized (outputQueue) {
				if (outputQueue.peekLast() != output) {
					outputQueue.addLast(output);
					output.retain();
				}
			}

			if (key != null) {
				key.setInterest(true, true);
			}
		}
	}

	protected abstract boolean canWrite(Operation op);
	
	public abstract void wroteBytes(long bytes);

	@Override
	public synchronized void write(SocketChannel channel) throws IOException {

		while (!outputQueue.isEmpty()) {

			DataBuffer buffer = outputQueue.peekFirst();
			try {

				int pos = buffer.getReadIndex();
				buffer.writeTo(channel);
				wroteBytes(buffer.getReadIndex() - pos);

			} catch (IOException e) {

				logger.warn("Writing to socket failed.", e);
				abort();
				return;
			}

			if (buffer.hasReadableBytes()) {
				// If we didn't write the whole thing, the socket won't accept
				// more
				break;
			} else {
				outputQueue.removeFirst().release();
			}
		}

		if (outputQueue.isEmpty()) {

			synchronized (keyLock) {
				key.setInterest(true, false);

				if (close) {
					logger.debug("Closing...");
					key.close();
				}
			}
		}
	}

	public boolean hasOutputQueued() {
		return !outputQueue.isEmpty();
	}

	@Override
	public ReactorKey getKey() {
		return key;
	}

	@Override
	public void setKey(ReactorKey key) {
		this.key = key;
		if (key != null) {
			key.setInterest(true, !outputQueue.isEmpty());
		}
	}

	public synchronized void close() {
		close = true;

		synchronized (keyLock) {

			if (key != null && !outputQueue.isEmpty()) {
				key.setInterest(true, true);
			}

			if (key != null && outputQueue.isEmpty()) {
				logger.debug("Closed");
				key.close();
			}
		}
	}

	@Override
	public void read(SocketChannel channel) throws IOException {

		if (buffer == null) {
			parser = newParser();
			buffer = new DataBuffer();
		}
		
		buffer.readFrom(channel);

		if (parser != null) {
			parseHeader(channel);
		} else {
			processBody();
		}
	}

	private void parseHeader(SocketChannel channel) {

		int pos = buffer.getWriteIndex();

		try {
			parser.parse(buffer);
		} catch (ParseException e) {
			logger.error("Aborting request due to malformed headers", e);
			abort();
			return;
		} catch (IOException e) {
			logger.error("Failed to read headers, aborting", e);
			abort();
			return;
		}

		wroteBytes(buffer.getWriteIndex() - pos);

		MessageHeader header = parser.getHeader();

		if (header != null) {

			logger.debug("Have full header...");
			logger.debug(header);

			buffer.release();
			buffer = null;

			processHeader(header, channel);
			
			parser = null;
		}
	}

	@Override
	public void abort() {
		if (buffer != null) {
			buffer.release();
			buffer = null;
		}
		
		for (DataBuffer buffer : outputQueue) {
			buffer.release();
		}
		
		outputQueue.clear();
	}
	
	@Override
	public Object keyLock() {
		return keyLock;
	}
	
	protected abstract MessageParser newParser();

	protected abstract void processHeader(MessageHeader header, SocketChannel channel);

	protected abstract void processBody();
}