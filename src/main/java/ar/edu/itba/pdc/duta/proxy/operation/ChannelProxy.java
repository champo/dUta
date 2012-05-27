package ar.edu.itba.pdc.duta.proxy.operation;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.net.OutputChannel;
import ar.edu.itba.pdc.duta.net.Server;
import ar.edu.itba.pdc.duta.proxy.ResponseChannelHandler;

public class ChannelProxy implements OutputChannel {

	private static final Logger logger = Logger.getLogger(ChannelProxy.class);

	private InetSocketAddress address;
	
	private Operation op;

	private ResponseChannelHandler channel;
	
	public ChannelProxy(InetSocketAddress address, Operation op) {
		super();
		this.address = address;
		this.op = op;
	}

	public void queueOutput(ByteBuffer buff) {
		
		if (channel == null) {
			openChannel();
		}
		
		logger.debug("Queueing output: " + buff);
		channel.queueOutput(buff);
	}

	private void openChannel() {
		logger.debug("Opening channel...");
		channel = Server.getConnectionPool().getConnection(address);
		channel.setOp(op);
	}
	
	public ResponseChannelHandler getChannel() {
		return channel;
	}
}
