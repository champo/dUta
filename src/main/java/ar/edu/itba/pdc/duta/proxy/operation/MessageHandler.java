package ar.edu.itba.pdc.duta.proxy.operation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.http.Grammar;
import ar.edu.itba.pdc.duta.http.MessageFactory;
import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.net.OutputChannel;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;

public class MessageHandler {

	private static final Logger logger = Logger.getLogger(MessageHandler.class);

	private List<OperationFilter> filters;

	private Message msg;

	private boolean needsBody;

	private OutputChannel outputChannel;

	private boolean complete;

	private BodyParser parser;
	
	private long size = 0;

	public MessageHandler(MessageHeader header, List<OperationFilter> filters, OutputChannel outputChannel) {
		this.filters = filters;
		this.msg = new Message(header);
		this.outputChannel = outputChannel;

		complete = false;

		for (OperationFilter filter : filters) {
			logger.debug("Filter " + filter.part);
			if (filter.interest.bytesRecieved() || filter.interest.full()) {
				needsBody = true;
			}
		}
		
		logger.debug("Needs body: " + needsBody);
	}

	public Message processHeader(Operation op) {

		for (OperationFilter filter : filters) {

			if (filter.interest.preProcess()) {
				Message result = filter.part.processHeader(op, msg.getHeader());
				if (result != null) {
					return result;
				}
			}
		}

		if (!createParser()) {
			return MessageFactory.build500();
		}
		
		DataBuffer buffer = new DataBuffer();
		msg.setBody(buffer);
		buffer.release();
		
		// TODO: Create the buffer as needed
		if (!needsBody) {
			Message res = writeHeader();
			if (res != null) {
				return res;
			}
		}

		checkCompletion();
		if (complete) {
			return filter(op);
		}

		return null;
	}

	private boolean createParser() {
		
		MessageHeader header = msg.getHeader();
 		
		String length = header.getField("Content-Length");
		try {
			Integer.valueOf(length);
			parser = new SimpleParser(msg);
		} catch (NumberFormatException e) {
			
			if (!Grammar.HTTP11.equalsIgnoreCase(header.getHTTPVersion())) {
				parser = new Http10Parser(msg);
			} else if (isChunked()) {
				parser = new ChunkedParser(msg, needsBody);
			} else if (header instanceof RequestHeader) {
				
				String type = header.getField("Content-Type");
				if (type == null || type.isEmpty()) {
					parser = new EmptyParser();
				} else {
					return false;
				}
			}
			
		}
		
		return parser != null;
	}

	private boolean isChunked() {
		// TODO Auto-generated method stub
		return false;
	}

	public Message forceCompletion(Operation op) {
		complete = true;

		return filter(op);
	}

	private void checkCompletion() {
		complete = parser.isComplete();
	}

	public Message append(Operation op) {
		
		try {
			size += parser.parse();
		} catch (IOException e) {
			return MessageFactory.build500();
		}
		
		if (!needsBody) {
			outputChannel.queueOutput(msg.getBody());
		} else {

			for (OperationFilter filter : filters) {

				if (filter.interest.bytesRecieved()) {
					Message result = filter.part.bytesRecieved(op, msg, size);
					if (result != null) {
						return result;
					}
				}
			}
		}

		checkCompletion();
		if (complete) {
			return filter(op);
		}

		return null;
	}

	private Message filter(Operation op) {

		if (!needsBody) {
			return null;
		}

		for (OperationFilter filter : filters) {

			if (filter.interest.full()) {
				Message result = filter.part.filter(op, msg);
				if (result != null) {
					return result;
				}
			}
		}

		Message res = writeHeader();
		if (res != null) {
			return res;
		}

		outputChannel.queueOutput(msg.getBody());

		return null;
	}

	private Message writeHeader() {

		logger.debug("Writing header " + msg.getHeader());
		try {
			DataBuffer buffer = new DataBuffer(msg.getHeader().toString().getBytes("ascii"));
			outputChannel.queueOutput(buffer);
			buffer.release();
			
		} catch (UnsupportedEncodingException e) {
			// If this happens, the world is screwed
			logger.error("Failed to encode header", e);

			// TODO: Return a 500 error
			return null;
		}

		return null;
	}

	public boolean isMessageComplete() {
		logger.debug("isMessageComplete " + (complete ? "true" : "false"));
		return complete;
	}

	public DataBuffer getBuffer() {
		return msg.getBody();
	}

	public void collect() {
		msg.setBody(null);
	}
}
