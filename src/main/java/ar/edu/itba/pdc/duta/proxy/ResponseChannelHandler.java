package ar.edu.itba.pdc.duta.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.net.AbstractChannelHandler;
import ar.edu.itba.pdc.duta.net.OutputChannel;
import ar.edu.itba.pdc.duta.net.Server;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;
import ar.edu.itba.pdc.duta.net.buffer.FixedDataBuffer;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class ResponseChannelHandler extends AbstractChannelHandler implements OutputChannel {

	private static Logger logger = Logger.getLogger(ResponseChannelHandler.class);
	
	private Operation op;

	private InetSocketAddress address;

	public ResponseChannelHandler(InetSocketAddress address) {
		this.address = address;
	}

	public synchronized void setOp(Operation op) {
		this.op = op;
	}

	@Override
	public synchronized void read(SocketChannel channel) throws IOException {
		
		DataBuffer buffer;
		if (op == null) {
			buffer = new FixedDataBuffer(4096);
		} else {
			buffer = op.getResponseBuffer();
		}
		int read = buffer.readFrom(channel);
		if (read == -1) {
			abort();
			return;
		}
		
		Stats.addServerTraffic(read);
		
		if (op != null) {
			op.addResponseData(buffer);
		}
	}

	public InetSocketAddress getAddress() {
		return address;
	}
	
	@Override
	public void close() {
		
		Server.getConnectionPool().remove(this);
		Stats.closeOutbound();
		
		super.close();
	}
	
	@Override
	public void abort() {
		
		logger.debug("Got closed, removing myself from the world!");
		if (op != null) {
			op.close();
		}
		
		close();
	}

	@Override
	public void wroteBytes(long bytes) {
		Stats.addServerTraffic(bytes);
	}

}
