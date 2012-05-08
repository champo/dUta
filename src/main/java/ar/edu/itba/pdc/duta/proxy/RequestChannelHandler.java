package ar.edu.itba.pdc.duta.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.parser.ParseException;
import ar.edu.itba.pdc.duta.http.parser.RequestParser;
import ar.edu.itba.pdc.duta.net.AbstractChannelHandler;
import ar.edu.itba.pdc.duta.net.Server;
import ar.edu.itba.pdc.duta.net.Server.Stats;

public class RequestChannelHandler extends AbstractChannelHandler {

	private static Logger logger = Logger.getLogger(RequestChannelHandler.class);

	private ByteBuffer inputBuffer = ByteBuffer.allocate(8192);

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
			close();
			return;
		}

		int pos = inputBuffer.position();
		inputBuffer.reset();
		inputBuffer.limit(pos);

		try {
			parser.parse();
		} catch (ParseException e) {
			logger.error("Aborting request due to malformed headers", e);
			close();
			return;
		}
		inputBuffer.limit(inputBuffer.capacity());

		MessageHeader header = parser.getHeader();
		if (header != null) {
			logger.debug("Got request, asking for responsee");
			String host = header.getField("Host");
			SocketAddress remote;
			
			//FIXME: These are proxy specific headers that make origin servers go boom, remove them in a clean way
			header.removeField("Proxy-Connection");
			header.removeField("Proxy-Authenticate");
			header.removeField("Proxy-Authorization");
			
			if (host.contains(":")) {
				String[] split = host.split(":");
				
				try {
					remote = new InetSocketAddress(split[0], Integer.parseInt(split[1]));
				} catch (NumberFormatException e) {
					// Force close!
					logger.error("Got an invalid port number in the Host header", e);
					close();
					return;
				}
			} else {				
				remote = new InetSocketAddress(host, 80);
			}
			

			header.setField("Connection", "close");
			ResponseChannelHandler response = new ResponseChannelHandler(this);

			try {
				Server.newConnection(remote, response);
				Stats.newOutbound();
			} catch (Exception e) {
				logger.error("Caught exception trying to create outbound connection.", e);
				close();
				return;
			}

			String request = header.toString();
			response.queueOutput(ByteBuffer.wrap(request.getBytes()));
		}
	}

	@Override
	public void close() {
		super.close();
		Stats.closeInbound();
	}
}
