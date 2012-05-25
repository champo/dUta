package ar.edu.itba.pdc.duta.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.net.AbstractChannelHandler;
import ar.edu.itba.pdc.duta.net.Server.Stats;

public class ResponseChannelHandler extends AbstractChannelHandler {

	private static Logger logger = Logger.getLogger(ResponseChannelHandler.class);
	
	private Operation op;
	
	public ResponseChannelHandler(Operation operation) {
		super();
		//FIXME: Refactor this class to allow for persistant connections
		this.op = operation;
	}

	@Override
	public void read(SocketChannel channel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(1000);
		if (channel.read(buffer) == -1) {
			logger.debug("Got full response. Closing channels.");
			
			close();
			op.close();
			
			Stats.closeOutbound();
			
			return;
		}
		
		buffer.flip();
		
		op.queueOutput(buffer);
	}

}
