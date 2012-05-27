package ar.edu.itba.pdc.duta.proxy;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.http.parser.ParseException;
import ar.edu.itba.pdc.duta.http.parser.RequestParser;
import ar.edu.itba.pdc.duta.net.AbstractChannelHandler;
import ar.edu.itba.pdc.duta.net.Server.Stats;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class RequestChannelHandler extends AbstractChannelHandler {

	private static Logger logger = Logger.getLogger(RequestChannelHandler.class);

	private ByteBuffer inputBuffer;

	private RequestParser parser;

	private Operation op;

	public RequestChannelHandler(SocketAddress address) {
		super(address);
	}

	@Override
	public void read(SocketChannel channel) throws IOException {

		if (inputBuffer == null) {
			inputBuffer = ByteBuffer.allocate(8192);
		}
		
		inputBuffer.mark();
		int read = channel.read(inputBuffer);
		if (read == -1) {
			close();
			return;
		}

		int pos = inputBuffer.position();
		inputBuffer.reset();
		inputBuffer.limit(pos);
		
		if (op == null) {
			processHeader();
		} else {
			
			op.addRequestData(inputBuffer);
			
			if (op.isRequestComplete()) {
				logger.debug("Detaching operation from request");
				// If it returned true, it means the request data should be complete
				op = null;
			}
			
			inputBuffer = null;
		}
	}

	private void processHeader() {

		if (parser == null) {
			parser = new RequestParser(inputBuffer);
		}
		
		try {
			parser.parse();
		} catch (ParseException e) {
			logger.error("Aborting request due to malformed headers", e);
			close();
			return;
		}

		MessageHeader header = parser.getHeader();
		if (header != null) {
			logger.debug("Have full header, creating op...");
			logger.debug(header);
			
			parser = null;
			op = new Operation(this);
			op.setRequestHeader((RequestHeader) header, inputBuffer);
			
			if (op.isRequestComplete()) {
				// If it returned true, it means the request data should be complete
				op = null;
			}
		}
		
		inputBuffer.limit(inputBuffer.capacity());
	}

	@Override
	public void close() {
		super.close();
		Stats.closeInbound();
	}
}
