package ar.edu.itba.pdc.duta.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class Reactor implements Runnable {

	private Selector selector;
	
	private ReentrantLock guard;

	private boolean run;
	
	public Reactor() throws IOException {
		selector = Selector.open();
		guard = new ReentrantLock();
	}

	public void addChannel(SocketChannel socket, ChannelHandler handler) throws IOException {

		while (!guard.tryLock()) {
			selector.wakeup();
		}
		
		try {
			socket.configureBlocking(false);
			SelectionKey key = socket.register(selector, SelectionKey.OP_READ);
			
			// Link key and handler
			key.attach(handler);
			handler.setKey(new ReactorKey(key));
		} finally {
			guard.unlock();
		}
	}
	
	@Override
	public void run() {
		
		run = true;
		while (run) {
			try {
				
				guard.lock();
				try {
					selector.select();
				} finally {
					guard.unlock();
				}
				
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
						
						// Remove the link between handler and key
						key.attach(null);
						handler.setKey(null);
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stop() {
		run = false;
		selector.wakeup();
	}
	
	@ThreadSafe
	public static class ReactorKey {
		
		private SelectionKey key;
		
		private ReactorKey(SelectionKey key) {
			this.key = key;
		}
		
		public void setInterest(boolean read, boolean write) {
			
			int ops = 0;
			
			if (read) {
				ops |= SelectionKey.OP_READ;
			}
			
			if (write) {
				ops |= SelectionKey.OP_WRITE;
			}
			
			if (key.isValid()) {
				key.interestOps(ops);
				key.selector().wakeup();
			}
		}

		public void close() {
			try {
				key.channel().close();
			} catch (IOException e) {
				System.out.println("Failed to close channel.\n" + e);
			}
		}
	}
}
