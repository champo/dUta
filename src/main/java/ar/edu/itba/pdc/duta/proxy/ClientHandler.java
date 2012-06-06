package ar.edu.itba.pdc.duta.proxy;

import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.http.parser.MessageParser;
import ar.edu.itba.pdc.duta.http.parser.RequestParser;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class ClientHandler extends AbstractChannelHandler {

	private static Logger logger = Logger.getLogger(ClientHandler.class);
	
	protected Operation currentOperation;
	
	public ClientHandler() {
		super();
	}

	@Override
	public void close() {
		super.close();
		Stats.closeInbound();
	}

	@Override
	public void abort() {
		super.abort();
		currentOperation = null;
		close();
	}

	@Override
	protected void wroteBytes(long bytes) {
		Stats.addClientTraffic(bytes);
	}

	public void operationComplete() {

		logger.debug("Detaching from op...");
		currentOperation = null;

		if (buffer != null) {
			buffer.release();
		}
		buffer = null;
	}

	@Override
	protected void processBody() {
		currentOperation.addClientBody();
	}

	@Override
	protected void processHeader(MessageHeader header, SocketChannel channel) {
		
		currentOperation = new Operation(this);
		
		buffer = currentOperation.setClientHeader((RequestHeader) header, channel);
		if (buffer != null) {
			buffer.retain();
		}
	}

	@Override
	protected MessageParser newParser() {
		return new RequestParser();
	}

}
