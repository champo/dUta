package ar.edu.itba.pdc.duta.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Reactor implements Runnable {

	private Selector selector;
	
	private Object guard;
	
	public Reactor() throws IOException {
		selector = Selector.open();
		guard = new Object();
	}

	public void addChannel(SocketChannel socket, ChannelHandler handler) throws IOException {
		socket.configureBlocking(false);
		synchronized (guard) {
			selector.wakeup();
			SelectionKey key = socket.register(selector, SelectionKey.OP_READ);
			key.attach(handler);
		}
	}

	@Override
	public void run() {
		
		while (true) {
			try {
				synchronized (guard) {
				}
				
				selector.select();
				
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
			}
		}
	}

	
}
