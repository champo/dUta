package ar.edu.itba.pdc.duta.proxy;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.net.Server;
import ar.edu.itba.pdc.duta.net.Server.Stats;

public class ConnectionResolver {
	
	private static final Logger logger = Logger.getLogger(ConnectionResolver.class);

	public ResponseChannelHandler getConnection(InetSocketAddress remote) {
		
		try {
			ResponseChannelHandler response = new ResponseChannelHandler();
			
			Server.newConnection(remote, response);
			Stats.newOutbound();
			
			return response;
		} catch (Exception e) {
			logger.error("Caught exception trying to create outbound connection.", e);
		}
		
		return null;
	}
	
}
