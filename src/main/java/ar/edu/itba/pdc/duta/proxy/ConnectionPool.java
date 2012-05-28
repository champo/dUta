package ar.edu.itba.pdc.duta.proxy;

import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.net.Server;

public class ConnectionPool {
	
	private static final Logger logger = Logger.getLogger(ConnectionPool.class);
	
	private Map<String, Queue<ResponseChannelHandler>> pool;
	
	public ConnectionPool() {
		super();
		this.pool = new HashMap<String, Queue<ResponseChannelHandler>>();
	}

	public ResponseChannelHandler getConnection(InetSocketAddress remote) {
		
		synchronized (pool) {
			
			String key = addressToKey(remote);
			Queue<ResponseChannelHandler> list = pool.get(key);
			if (list != null && list.size() > 0) {
				
				if (list.size() == 1) {
					pool.remove(key);
				}
				logger.debug("Giving connection " + list.peek());
				
				return list.poll();
			}

			return newConnection(remote);
		}
		
	}
	
	public void registerConnection(ResponseChannelHandler handler) {
		
		synchronized (pool) {
			handler.setOp(null);
			
			String key = addressToKey(handler.getAddress());
			Queue<ResponseChannelHandler> list = pool.get(key);
			
			if (list != null) {
				
				if (list.size() < 4) {
					logger.debug("Storing connection to " + key);
					list.add(handler);
				}
			} else {
				list = new ArrayDeque<ResponseChannelHandler>(4);
				list.add(handler);
				logger.debug("Storing connection to " + key);
				
				pool.put(key, list);
			}
		}
	}
	
	private ResponseChannelHandler newConnection(InetSocketAddress remote) {
		
		logger.debug("Boring new connection");
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
			
			String key = addressToKey(handler.getAddress());
			Queue<ResponseChannelHandler> list = pool.get(key);
			
			if (list != null) {
				list.remove(handler);
				
				if (list.size() == 0) {
					pool.remove(key);
				}
			}
		}
	}

	private String addressToKey(InetSocketAddress remote) {
		return remote.getHostString() + ":" + remote.getPort();
	}
}
