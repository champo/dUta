package ar.edu.itba.pdc.duta.proxy;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.ResponseHeader;
import ar.edu.itba.pdc.duta.http.parser.MessageParser;
import ar.edu.itba.pdc.duta.http.parser.ResponseParser;
import ar.edu.itba.pdc.duta.net.Server;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class ServerHandler extends AbstractChannelHandler {

	private static Logger logger = Logger.getLogger(ServerHandler.class);

	private InetSocketAddress address;
	
	protected Operation currentOperation;

	public ServerHandler(InetSocketAddress address) {
		this.address = address;
	}

	public void setCurrentOperation(Operation op) {
		this.currentOperation = op;
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	@Override
	public void close() {

		Server.getConnectionPool().remove(this);
		Stats.closeOutbound();

		super.close();
	}

	@Override
	public void abort() {

		super.abort();

		logger.debug("Got closed, removing myself from the world!");
		if (currentOperation != null) {
			currentOperation.close();
			buffer = null;
		}

		close();
	}

	@Override
	public void wroteBytes(long bytes) {
		Stats.addServerTraffic(bytes);
	}

	@Override
	protected void processBody() {
		if (currentOperation != null) {
			currentOperation.addServerBody();
		}
	}

	@Override
	protected void processHeader(MessageHeader header, SocketChannel channel) {
		
		if (currentOperation == null) {
			return;
		}
		
		buffer = currentOperation.setServerHeader((ResponseHeader) header);
		if (buffer != null) {
			buffer.retain();
		}
	}
	
	public void operationComplete() {
		currentOperation = null;
		
		if (buffer != null) {
			buffer.release();
			buffer = null;
		}
	}

	@Override
	protected MessageParser newParser() {
		return new ResponseParser();
	}

	@Override
	protected boolean canWrite(Operation op) {
		return currentOperation == op;
	}

}
