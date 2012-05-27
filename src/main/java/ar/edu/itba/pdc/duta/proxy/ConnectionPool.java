package ar.edu.itba.pdc.duta.proxy;

import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.net.Server;
import ar.edu.itba.pdc.duta.net.Server.Stats;

public class ConnectionPool {
	
	private static final Logger logger = Logger.getLogger(ConnectionPool.class);
	
	private Map<SocketAddress, Queue<ResponseChannelHandler>> pool;
	
	public ConnectionPool() {
		super();
		this.pool = new HashMap<SocketAddress, Queue<ResponseChannelHandler>>();
	}

	public ResponseChannelHandler getConnection(SocketAddress remote) {
		
		synchronized (pool) {
			
			Queue<ResponseChannelHandler> list = pool.get(remote);
			if (list != null && list.size() > 0) {
				
				if (list.size() == 1) {
					pool.remove(remote);
				}
				
				return list.peek();
			}

			return newConnection(remote);
		}
		
	}
	
	public void registerConnection(ResponseChannelHandler handler) {
		
		synchronized (pool) {
			handler.setOp(null);
			
			SocketAddress addr = handler.getAddress();
			Queue<ResponseChannelHandler> list = pool.get(addr);
			
			if (list != null) {
				
				if (list.size() < 4) {
					logger.debug("Storing connection to " + addr);
					list.add(handler);
				}
			} else {
				list = new ArrayDeque<ResponseChannelHandler>(4);
				list.add(handler);
				logger.debug("Storing connection to " + addr);
				
				pool.put(addr, list);
			}
		}
	}
	
	private ResponseChannelHandler newConnection(SocketAddress remote) {
		
		try {
			ResponseChannelHandler response = new ResponseChannelHandler(remote);
			
			Server.newConnection(remote, response);
			Stats.newOutbound();
			
			return response;
		} catch (Exception e) {
			logger.error("Caught exception trying to create outbound connection.", e);
		}
		
		return null;
	}

	public void remove(ResponseChannelHandler handler) {
		
		synchronized (pool) {
			handler.setOp(null);
			
			SocketAddress addr = handler.getAddress();
			Queue<ResponseChannelHandler> list = pool.get(addr);
			
			if (list != null) {
				list.remove(handler);
				
				if (list.size() == 0) {
					pool.remove(addr);
				}
			}
		}
	}
	
}
