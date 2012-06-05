package ar.edu.itba.pdc.duta.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import net.jcip.annotations.ThreadSafe;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.proxy.ClientHandler;
import ar.edu.itba.pdc.duta.proxy.ConnectionPool;
import ar.edu.itba.pdc.duta.proxy.filter.Filters;


@ThreadSafe
public class Server {
	
	public static final int ADMIN_PORT = 1337;

	public static final Logger logger = Logger.getLogger(Server.class);

	private static Server instance;

	private ReactorPool reactorPool;

	private ConnectionPool resolver;

	private Filters filters;


	private Server() {
		super();
		resolver = new ConnectionPool();
		filters = new Filters();
	}

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
			listen(port, selector);
			listen(ADMIN_PORT, selector);
			
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
							
							Stats.newInbound();
							ChannelHandler handler = new ClientHandler();
							
							socket.socket().setTcpNoDelay(true);
							getReactor().addChannel(socket, handler);
							
							Stats.log();
						}
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		reactorPool.close();
		reactorPool = null;
	}

	/**
	 * @param port
	 * @param selector
	 * @throws IOException
	 * @throws ClosedChannelException
	 */
	private void listen(int port, Selector selector) throws IOException,
			ClosedChannelException {
		ServerSocketChannel serverChannel = selector.provider().openServerSocketChannel();
		
		serverChannel.configureBlocking(false);
		serverChannel.socket().bind(new InetSocketAddress(port));
		
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	private void runReactors() throws IOException {
		int threads = 2 * Runtime.getRuntime().availableProcessors();
		//int threads = 2;
		reactorPool = new ReactorPool(threads);
		reactorPool.start();
	}

	private Reactor getReactor() {
		return reactorPool.get();
	}
	
	public static void main(String[] args) throws IOException {
		Server.run();
		System.exit(0);
	}

	private static void run() {
		instance = new Server();
		instance.start();
	}
	
	public static void registerChannel(SocketChannel channel, ChannelHandler handler) throws IOException {
		instance.getReactor().addChannel(channel, handler);
	}

	public static void newConnection(SocketAddress remote, ChannelHandler handler) throws IOException {
		SocketChannel socket = SocketChannel.open();
		
		socket.configureBlocking(false);
		socket.socket().setTcpNoDelay(true);
		socket.connect(remote);
		
		instance.getReactor().addChannel(socket, handler);
	}
	
	public static ConnectionPool getConnectionPool() {
		return instance.resolver;
	}

	public static Filters getFilters() {
		return instance.filters;
	}

}
