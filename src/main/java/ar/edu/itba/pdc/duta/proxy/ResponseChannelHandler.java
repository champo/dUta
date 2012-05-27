package ar.edu.itba.pdc.duta.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.net.AbstractChannelHandler;
import ar.edu.itba.pdc.duta.net.Server.Stats;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class ResponseChannelHandler extends AbstractChannelHandler {

	private static Logger logger = Logger.getLogger(ResponseChannelHandler.class);
	
	private Operation op;
	
	public ResponseChannelHandler() {
		super();
	}
	
	public void setOp(Operation op) {
		this.op = op;
	}

	@Override
	public void read(SocketChannel channel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		if (channel.read(buffer) == -1) {
			logger.debug("Got full response. Closing channels.");
			
			close();
			op.close();
			
			Stats.closeOutbound();
			
			return;
		}
		
		buffer.flip();
		
		op.addResponseData(buffer);
	}

}
