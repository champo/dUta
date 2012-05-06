package ar.edu.itba.pdc.duta.net;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

public class SocketPool {

	private static final Logger logger = Logger.getLogger(SocketPool.class);

	private Queue<SocketChannel> sockets;
	
	private int max;
	
	public SocketPool(int count, int max) throws IOException {
		assert count <= max;
		this.max = max;
		sockets = new LinkedBlockingQueue<SocketChannel>(max);
		
		for (int i = 0; i < count; i++) {
			sockets.add(SocketChannel.open());
		}
	}
	
	public SocketChannel get() {
		SocketChannel channel = sockets.poll();
		
		if (channel == null) {
			try {
				return SocketChannel.open();
			} catch (IOException e) {
				return null;
			}
		}
		
		try {
			channel.socket().setTcpNoDelay(true);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return channel;
	}
	
	public void put(SocketChannel channel) {
		
		if (sockets.size() > max) {
			
			try {
				channel.close();
			} catch (IOException e) {
				logger.warn("Caught exception closing a channel in the socket pool", e);
			}
			return;
		}
		
		sockets.add(channel);
	}
}
