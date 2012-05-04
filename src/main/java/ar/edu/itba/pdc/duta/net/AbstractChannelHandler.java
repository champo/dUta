package ar.edu.itba.pdc.duta.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

import ar.edu.itba.pdc.duta.net.Reactor.ReactorKey;

public abstract class AbstractChannelHandler implements ChannelHandler {
	
	private ReactorKey key;
	
	private Queue<ByteBuffer> outputQueue;
	
	public AbstractChannelHandler() {
		outputQueue = new LinkedList<ByteBuffer>();
	}
	
	public void queueOutput(ByteBuffer output) {
		outputQueue.add(output);
		key.setInterest(true, true);
	}

	@Override
	public void write(SocketChannel channel) throws IOException {
		
		while (!outputQueue.isEmpty()) {
			
			ByteBuffer buffer = outputQueue.peek();
			channel.write(buffer);
			
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
	}
	
}