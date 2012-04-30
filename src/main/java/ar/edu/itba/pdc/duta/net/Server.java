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

import ar.edu.itba.pdc.duta.http.RequestParser;

public class Server {

	private Reactor reactor;
	
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
							
							logger.info("Acception connection from " + socket.socket().getInetAddress());
							ChannelHandler handler = new ChannelHandler() {
								
								private ByteBuffer readBuffer = ByteBuffer.allocate(1000);
								
								private RequestParser parser = null;
								
								@Override
								public void write(SocketChannel channel) {
								}
								
								@Override
								public void read(SocketChannel channel) throws IOException {
									readBuffer.mark();
									int result = channel.read(readBuffer);
									
									if (result == -1) {
										channel.close();
									}
									
									readBuffer.reset();
									
									System.out.println(readBuffer);
									
									if (parser == null) {
										parser = new RequestParser(readBuffer);
									}
									
									try {
										if (parser.parse()) {
											System.out.println(parser.getHeader());
										}
									} catch (Exception e) {
										channel.close();
										e.printStackTrace();
									}
									
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
		//FIXME: Be able to kill the thread if something goes wrong
		reactor = new Reactor();
		new Thread(reactor).start();
	}

	private Reactor getReactor() {
		return reactor;
	}
	
	public static void main(String[] args) throws IOException {
		new Server().start();
		System.exit(0);
	}
}
