package ar.edu.itba.pdc.duta.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.net.AbstractChannelHandler;
import ar.edu.itba.pdc.duta.net.Server;
import ar.edu.itba.pdc.duta.net.Server.Stats;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class ResponseChannelHandler extends AbstractChannelHandler {

	private static Logger logger = Logger.getLogger(ResponseChannelHandler.class);
	
	private Operation op;

	private InetSocketAddress address;

	public ResponseChannelHandler(InetSocketAddress address) {
		this.address = address;
	}

	public void setOp(Operation op) {
		this.op = op;
	}

	@Override
	public void read(SocketChannel channel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		if (channel.read(buffer) == -1) {
			
			logger.debug("Got closed, removing myself from the world!");
			if (op != null) {
				op.close();
			}
			
			Stats.closeOutbound();
			
			Server.getConnectionPool().remove(this);
			close();
			
			return;
		}
		
		buffer.flip();
		
		if (op != null) {
			op.addResponseData(buffer);
		}
	}

	public InetSocketAddress getAddress() {
		return address;
	}

}
