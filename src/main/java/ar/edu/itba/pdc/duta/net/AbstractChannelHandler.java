package ar.edu.itba.pdc.duta.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import net.jcip.annotations.ThreadSafe;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.net.Reactor.ReactorKey;

@ThreadSafe
public abstract class AbstractChannelHandler implements ChannelHandler {

	private static Logger logger = Logger.getLogger(AbstractChannelHandler.class);

	protected ReactorKey key;

	private Queue<ByteBuffer> outputQueue;

	protected boolean close = false;

	protected Object keyLock;

	public AbstractChannelHandler() {
		outputQueue = new LinkedBlockingQueue<ByteBuffer>();
		key = null;
		keyLock = new Object();
	}

	public void queueOutput(ByteBuffer output) {
		outputQueue.add(output);
		
		synchronized (keyLock) {
			if (key != null) {
				key.setInterest(true, true);
			}
		}
	}

	@Override
	public void write(SocketChannel channel) throws IOException {

		while (!outputQueue.isEmpty()) {

			ByteBuffer buffer = outputQueue.peek();
			try {
				channel.write(buffer);
			} catch (IOException e) {
				logger.warn("Writing to socket failed.", e);
				System.err.println(e);
				// This should mean the pipe was broken. We bail in that case.
				
				synchronized (keyLock) {
					key.close();
				}
				return;
			}

			if (buffer.hasRemaining()) {
				// If we didn't write the whole thing, the socket won't accept more
				break;
			} else {
				outputQueue.remove();
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

	public ReactorKey getKey() {
		return key;
	}

	@Override
	public void setKey(ReactorKey key) {

		synchronized (keyLock) {
			this.key = key;
			if (key != null) {
				key.setInterest(true, !outputQueue.isEmpty());
			}
		}
	}

	public void close() {
		close = true;

		synchronized (keyLock) {
			if (key != null && outputQueue.isEmpty()) {
				logger.debug("Closed");
				key.close();
			}
		}
	}

}