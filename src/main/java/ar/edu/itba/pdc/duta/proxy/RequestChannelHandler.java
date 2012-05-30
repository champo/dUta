package ar.edu.itba.pdc.duta.proxy;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.http.parser.ParseException;
import ar.edu.itba.pdc.duta.http.parser.RequestParser;
import ar.edu.itba.pdc.duta.net.AbstractChannelHandler;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class RequestChannelHandler extends AbstractChannelHandler {

	private static Logger logger = Logger.getLogger(RequestChannelHandler.class);

	private RequestParser parser;

	private Operation op;

	private DataBuffer buffer;

	@Override
	public void read(SocketChannel channel) throws IOException {

		if (op == null) {
			op = new Operation(this);
			buffer = op.getRequestBuffer();
			parser = new RequestParser(buffer);
		} else if (parser == null) {
			buffer = op.getRequestBuffer();
		}
		
		int read = buffer.readFrom(channel);
		if (read == -1) {
			close();
			return;
		}
		
		Stats.addClientTraffic(read);

		if (parser != null) {
			processHeader();
		} else {
			
			op.addRequestData(buffer);
			
			if (op.isRequestComplete()) {
				logger.debug("Detaching operation from request");
				// If it returned true, it means the request data should be complete
				op = null;
			}
		}
	}

	private void processHeader() {

		if (parser == null) {
			parser = new RequestParser(buffer);
		}
		
		try {
			parser.parse();
		} catch (ParseException e) {
			logger.error("Aborting request due to malformed headers", e);
			close();
			return;
		} catch (IOException e) {
			logger.error("Failed to read headers, aborting", e);
			close();
			return;
		}

		MessageHeader header = parser.getHeader();
		if (header != null) {
			logger.debug("Have full header, giving to op...");
			logger.debug(header);
			
			op.setRequestHeader((RequestHeader) header, buffer);
			
			parser = null;
			buffer = null;
			
			if (op.isRequestComplete()) {
				// If it returned true, it means the request data should be complete
				op = null;
			}
		}
		
	}

	@Override
	public void close() {
		super.close();
		Stats.closeInbound();
	}
	

	@Override
	public void wroteBytes(long bytes) {
		Stats.addClientTraffic(bytes);
	}
}
