package ar.edu.itba.pdc.duta.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import net.jcip.annotations.ThreadSafe;
import ar.edu.itba.pdc.duta.net.Reactor.ReactorKey;

@ThreadSafe
public abstract class AbstractChannelHandler implements ChannelHandler {
	
	protected ReactorKey key;
	
	private Queue<ByteBuffer> outputQueue;
	
	public AbstractChannelHandler() {
		outputQueue = new LinkedBlockingQueue<ByteBuffer>();
		key = null;
	}
	
	public void queueOutput(ByteBuffer output) {
		outputQueue.add(output);
		if (key != null) {
			key.setInterest(true, true);
		}
	}

	@Override
	public void write(SocketChannel channel) throws IOException {
		
		while (!outputQueue.isEmpty()) {
			
			ByteBuffer buffer = outputQueue.peek();
			try {
				channel.write(buffer);
			} catch (IOException e) {
				// This should mean the pipe was broken. We bail in that case.
				channel.close();
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
			key.setInterest(true, false);
		}
	}

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
	
}