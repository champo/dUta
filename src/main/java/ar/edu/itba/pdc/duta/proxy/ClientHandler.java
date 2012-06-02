package ar.edu.itba.pdc.duta.proxy;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.http.parser.ParseException;
import ar.edu.itba.pdc.duta.http.parser.RequestParser;
import ar.edu.itba.pdc.duta.net.AbstractChannelHandler;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;
import ar.edu.itba.pdc.duta.net.buffer.WrappedDataBuffer;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class ClientHandler extends AbstractChannelHandler {

	private static Logger logger = Logger.getLogger(ClientHandler.class);

	private RequestParser parser;

	private Queue<Operation> ops;

	private DataBuffer buffer; 
	
	private Operation currentOp;

	public ClientHandler() {
		super();
		ops = new ArrayDeque<Operation>();
	}

	@Override
	public void read(SocketChannel channel) throws IOException {

		if (currentOp == null) {
			currentOp = new Operation(this);
			ops.add(currentOp);
			
			parser = new RequestParser();
		}

		buffer = currentOp.getRequestBuffer();

		int read = buffer.readFrom(channel);
		
		if (read == -1) {
			abort();
			return;
		}

		Stats.addClientTraffic(read);

		if (parser != null) {
			processHeader();
		}

		if (currentOp != null) {
			
			if (parser == null && buffer.hasReadableBytes()) {
			
				//TODO: If it is complete, a new one should start and be queue'd
				if  (!currentOp.isRequestComplete()) {
					currentOp.addRequestData(buffer);
				} else {
					logger.warn("Got unexpected data for a request");
					logger.warn(buffer.toString());
				}
			}
			
			if (currentOp.isRequestComplete()) {
				currentOp = null;
			}
		}
	}

	private void processHeader() {

		try {
			parser.parse(buffer);
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

			currentOp.setRequestHeader((RequestHeader) header);

			parser = null;
			if (buffer.hasReadableBytes()) {
				buffer = new WrappedDataBuffer(buffer, buffer.getReadIndex(), buffer.remaining());
			}
		}
	}

	@Override
	public void close() {
		super.close();
		Stats.closeInbound();
	}
	
	@Override
	public void abort() {
		currentOp = null;
		for (Operation op : ops) {
			op.abort();
		}
		
		close();
	}
	
	@Override
	public void wroteBytes(long bytes) {
		Stats.addClientTraffic(bytes);
	}

	public void operationComplete() {
		logger.debug("Detaching from op...");
		ops.poll();
	}
	
	private static class Request {
		
		Operation op;
		
		
	}
}
