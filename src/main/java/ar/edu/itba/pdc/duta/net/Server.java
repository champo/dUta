package ar.edu.itba.pdc.duta.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class Server {

	private ReactorPool reactorPool;
	
	public void start() {
		
		Logger logger = Logger.getLogger(Server.class);
		try {
			runReactors();
		} catch (IOException e) {
			logger.fatal("Failed to start Reactors", e);
			return;
		}
		
		try {
			int port = 9999;
			
			Selector selector = Selector.open();
			ServerSocketChannel serverChannel = selector.provider().openServerSocketChannel();
			
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress(port));
			
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			logger.info("Starting server on port: " + port);
			
			while (true) {
				if (selector.select() > 0) {
					Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
					while (keys.hasNext()) {
						SelectionKey key = keys.next();
						keys.remove();

						ServerSocketChannel channel = (ServerSocketChannel) key.channel();
						SocketChannel socket = channel.accept();
						if (socket != null) {
							
							ChannelHandler handler = new AbstractChannelHandler() {
								
								@Override
								public void read(SocketChannel channel) throws IOException {
									
									ByteBuffer readBuffer = ByteBuffer.allocate(1000);
									int result = channel.read(readBuffer);
									if (result == -1) {
										channel.close();
										return;
									}

									readBuffer.flip();
									queueOutput(readBuffer);
								}
								
							};
							
							getReactor().addChannel(socket, handler);
						}
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void runReactors() throws IOException {
		int threads = 2 * Runtime.getRuntime().availableProcessors();
		reactorPool = new ReactorPool(threads);
		reactorPool.start();
	}

	private Reactor getReactor() {
		return reactorPool.get();
	}
	
	public static void main(String[] args) throws IOException {
		new Server().start();
		System.exit(0);
	}
}
