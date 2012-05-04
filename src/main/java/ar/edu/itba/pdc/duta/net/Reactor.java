package ar.edu.itba.pdc.duta.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

public class Reactor implements Runnable {

	private Selector selector;
	
	private ReentrantLock guard;
	
	public Reactor() throws IOException {
		selector = Selector.open();
		guard = new ReentrantLock();
	}

	public void addChannel(SocketChannel socket, ChannelHandler handler) throws IOException {
		socket.configureBlocking(false);

		while (!guard.tryLock()) {
			selector.wakeup();
		}
		
		try {
			SelectionKey key = socket.register(selector, SelectionKey.OP_READ);
			key.attach(handler);
		} finally {
			guard.unlock();
		}
	}
	
	@Override
	public void run() {
		
		while (true) {
			try {
				
				guard.lock();
				selector.select();
				guard.unlock();
				
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
				while (keys.hasNext()) {
					SelectionKey key = keys.next();
					keys.remove();
					
					SocketChannel channel = (SocketChannel) key.channel();
					ChannelHandler handler = (ChannelHandler) key.attachment();
					
					if (key.isValid() && key.isReadable()) {
						handler.read(channel);
					}
					
					if (key.isValid() && key.isWritable()) {
						handler.write(channel);
					}
					
					if (!channel.isOpen()) {
						key.cancel();
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (guard.isHeldByCurrentThread()) {
					guard.unlock();
				}
			}
		}
	}
}
