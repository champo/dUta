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
	
	public static int adminPort = 1337;

	public static final Logger logger = Logger.getLogger(Server.class);

	private static Server instance;

	private ReactorPool reactorPool;

	private ConnectionPool resolver;

	private Filters filters;

	private InetSocketAddress connectTo;

	private int port;

	private Server(int port, int adminPort, InetSocketAddress connectTo) {
		super();
		resolver = new ConnectionPool();
		filters = new Filters();

		Server.adminPort = adminPort;
		this.port = port;
		this.connectTo = connectTo;
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
			Selector selector = Selector.open();
			listen(port, selector);
			listen(adminPort, selector);
			
			logger.info("Starting server on port: " + port);
			
			while (true) {
				if (selector.select() > 0) {
					Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
					while (keys.hasNext()) {
						SelectionKey key = keys.next();
						keys.remove();

						ServerSocketChannel channel = (ServerSocketChannel) key.channel();
						
						SocketChannel socket;
						try {
							socket = channel.accept();
						} catch (Exception e) {
							logger.fatal("Failed to accept incoming", e);
							continue;
						}
						
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
		
		InetSocketAddress connectTo = null;
		int port = 9999;
		int adminPort = 1337;
		
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("--chain=")) {
				
				String chain = arg.substring(arg.indexOf('=') + 1);
				String[] split = chain.split(":");
				
				if (split.length != 2) {
					System.out.println("'" + chain + "' is not a valid address");
					usage();
					return;
				}
				
				try {
					connectTo = new InetSocketAddress(split[0], Integer.parseInt(split[1]));
				} catch (Exception e) {
					System.out.println("'" + chain + "' is not a valid address");
					usage();
					return;
				}
				
			} else if (arg.startsWith("--port=")) {
				
				String portString = arg.substring(arg.indexOf('=') + 1);
				
				try {
					port = Integer.parseInt(portString);
				} catch (NumberFormatException e) {
					System.out.println("'" + portString + "' is not a valid port number");
					usage();
					return;
				}
				
			} else if (arg.startsWith("--admin-port=")) {

				String portString = arg.substring(arg.indexOf('=') + 1);
				
				try {
					adminPort = Integer.parseInt(portString);
				} catch (NumberFormatException e) {
					System.out.println("'" + portString + "' is not a valid port number");
					usage();
					return;
				}

			}
			
		}
		
		System.out.println("Listen port: " + port);
		System.out.println("Admin port: " + adminPort);
		if (connectTo != null) {
			System.out.println("Chaining to: " + connectTo);
		}
		
		Server.run(port, adminPort, connectTo);
		System.exit(0);
	}

	private static void usage() {
		System.out.println("Options:");
		System.out.println("\t--chain=ip:port    Chain to another proxy");
		System.out.println("\t--port=port        Listen for requests on `port`");
		System.out.println("\t--admin-port=port  Listen for admin requests on `port`");
	}

	private static void run(int port, int adminPort, InetSocketAddress connectTo) {
		instance = new Server(port, adminPort, connectTo);
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
	
	public static InetSocketAddress getChainAddress() {
		return instance.connectTo;
	}

}
