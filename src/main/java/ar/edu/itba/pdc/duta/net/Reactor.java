package ar.edu.itba.pdc.duta.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import net.jcip.annotations.ThreadSafe;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.net.Server.Stats;

@ThreadSafe
public class Reactor implements Runnable {

	private static Logger logger = Logger.getLogger(Reactor.class);
	
	private Selector selector;
	
	private Object guard;
	
	private boolean run;
	
	public Reactor() throws IOException {
		selector = Selector.open();
		guard = new Object();
	}

	public void addChannel(SocketChannel socket, ChannelHandler handler) throws IOException {

		synchronized (guard) {
			selector.wakeup();
			
			socket.configureBlocking(false);
			int ops = SelectionKey.OP_READ;
			if (!socket.isConnected()) {
				ops = SelectionKey.OP_CONNECT;
			}
			
			SelectionKey key = socket.register(selector, ops, handler);
			handler.setKey(new ReactorKey(key));
		}
	}
	
	@Override
	public void run() {
		
		run = true;
		while (run) {
			try {

				synchronized (guard) {
					Stats.log();
				}
				selector.select();
				
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
				while (keys.hasNext()) {
					SelectionKey key = keys.next();
					keys.remove();
					
					SocketChannel channel = (SocketChannel) key.channel();
					ChannelHandler handler = (ChannelHandler) key.attachment();
					
					if (key.isValid() && key.isConnectable()) {
						channel.finishConnect();
						handler.getKey().setCachedOps();
						continue;
					}
					
					if (key.isValid() && key.isReadable()) {
						handler.read(channel);
					}
					
					if (key.isValid() && key.isWritable()) {
						handler.write(channel);
					}
					
					if (!key.isValid()) {
						channel.close();
						
						// Remove the link between handler and key
						key.attach(null);
					}
				}

			} catch (IOException e) {
				logger.warn("Caught exception on the reactor run loop.", e);
			}
		}
	}
	
	public void stop() {
		run = false;
		selector.wakeup();
	}
	
	@ThreadSafe
	public class ReactorKey {
		
		private SelectionKey key;
		
		private int ops;
		
		private ReactorKey(SelectionKey key) {
			this.key = key;
		}
		
		public synchronized void setInterest(boolean read, boolean write) {

			ops = 0;

			if (read) {
				ops |= SelectionKey.OP_READ;
			}

			if (write) {
				ops |= SelectionKey.OP_WRITE;
			}

			SocketChannel channel = (SocketChannel) key.channel();
			if (channel.isConnected() && key.isValid()) {
				key.interestOps(ops);
				selector.wakeup();
			}
		}
		
		/**
		 * Set the ops stored from the Handler.
		 *
		 * The Reactor may ignore the ops request by the handler.
		 * In that case, those ops are stored and re set after by calling this method.
		 */
		protected synchronized void setCachedOps() {
			
			key.interestOps(ops);
			selector.wakeup();
		}

		public synchronized void close() {
			
			key.cancel();
			try {
				key.channel().close();
			} catch (IOException e) {
				logger.warn("Caught exception closing a channel.", e);
			}
			
			selector.wakeup();
		}
	}
}
