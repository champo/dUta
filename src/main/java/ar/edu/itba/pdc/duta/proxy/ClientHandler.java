package ar.edu.itba.pdc.duta.proxy;

import java.util.ArrayDeque;
import java.util.Queue;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.http.parser.MessageParser;
import ar.edu.itba.pdc.duta.http.parser.RequestParser;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class ClientHandler extends AbstractChannelHandler {

	private static Logger logger = Logger.getLogger(ClientHandler.class);

	private Queue<Operation> ops;

	private Operation currentOperation;

	public ClientHandler() {
		super();
		ops = new ArrayDeque<Operation>();
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
		buffer = null;
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

	@Override
	protected void processBody() {
		currentOperation.addClientBody();

		if (currentOperation.isClientMessageComplete()) {
			currentOperation = null;
			buffer = null;
		}
	}

	@Override
	protected void processHeader(MessageHeader header) {
		currentOperation = new Operation(this);
		buffer = currentOperation.setClientHeader((RequestHeader) header);
		if (buffer != null) {
			buffer.retain();
		}
	}

	@Override
	protected MessageParser newParser() {
		return new RequestParser();
	}
}
