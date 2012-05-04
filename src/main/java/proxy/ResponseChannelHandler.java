package proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import ar.edu.itba.pdc.duta.net.AbstractChannelHandler;

public class ResponseChannelHandler extends AbstractChannelHandler {

	private RequestChannelHandler request;
	
	public ResponseChannelHandler(RequestChannelHandler request) {
		super();
		this.request = request;
	}

	@Override
	public void read(SocketChannel channel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(1000);
		if (channel.read(buffer) == -1) {
			channel.close();
			request.close();
		}
		
		buffer.rewind();
		
		request.queueOutput(buffer);
	}

}
