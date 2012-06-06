package ar.edu.itba.pdc.duta.proxy.operation;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.net.OutputChannel;
import ar.edu.itba.pdc.duta.net.Server;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;
import ar.edu.itba.pdc.duta.proxy.ServerHandler;

public class ChannelProxy implements OutputChannel {

	private static final Logger logger = Logger.getLogger(ChannelProxy.class);

	private InetSocketAddress address;

	private Operation op;

	private ServerHandler channel;

	public ChannelProxy(InetSocketAddress address, Operation op) {
		super();
		this.address = address;
		this.op = op;
	}

	@Override
	public void queueOutput(DataBuffer buff) {

		if (channel == null) {
			openChannel();
		}

		channel.queueOutput(buff);
	}

	private void openChannel() {
		
		logger.debug("Opening channel...");
		channel = Server.getConnectionPool().getConnection(address);
		if (channel == null) {
			throw new RuntimeException("Failed to resolve");
		} else {
			channel.attachTo(op, op.getLock());
		}
	}

	public ServerHandler getChannel() {
		return channel;
	}

	public InetSocketAddress getAddress() {
		return address;
	}
}
