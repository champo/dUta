package proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.parser.ParseException;
import ar.edu.itba.pdc.duta.http.parser.RequestParser;
import ar.edu.itba.pdc.duta.net.AbstractChannelHandler;
import ar.edu.itba.pdc.duta.net.Server;

public class RequestChannelHandler extends AbstractChannelHandler {

	private ByteBuffer inputBuffer = ByteBuffer.allocate(1000);

	private RequestParser parser;
	
	public RequestChannelHandler() {
		super();
		parser = new RequestParser(inputBuffer);
	}

	@Override
	public void read(SocketChannel channel) throws IOException {
		
		inputBuffer.mark();
		int read = channel.read(inputBuffer);
		if (read == -1) {
			channel.close();
			return;
		}
		
		int pos = inputBuffer.position();
		inputBuffer.reset();
		inputBuffer.limit(pos);
		
		if (!inputBuffer.hasRemaining()) {
			System.out.println("We read a buffer that has no data. WTF");
			System.out.println(inputBuffer);
			System.exit(1);
		}
		
		try {
			parser.parse();
		} catch (ParseException e) {
			System.out.println("Aborting request due to malformed headers\n" + e);
			channel.close();
			return;
		}
		inputBuffer.limit(inputBuffer.capacity());
		
		MessageHeader header = parser.getHeader();
		if (header != null) {
			SocketAddress remote = new InetSocketAddress(header.getField("Host"), 80);

			header.setHeader("Connection", "close");
			SocketChannel outChannel = SocketChannel.open(remote);
			ResponseChannelHandler response = new ResponseChannelHandler(this);
			
			Server.registerChannel(outChannel, response);
			
			String request = header.toString();
			response.queueOutput(ByteBuffer.wrap(request.getBytes()));
		}
	}

	public void close() {
		key.close();
	}

}
