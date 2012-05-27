package ar.edu.itba.pdc.duta.proxy.operation;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.net.OutputChannel;

public class FilterChain {
	
	private static final Logger logger = Logger.getLogger(FilterChain.class);
	
	private List<OperationFilter> filters;
	
	private Message msg;
	
	private boolean needsBody;

	private OutputChannel outputChannel;

	private boolean complete;
	
	public FilterChain(MessageHeader header, List<OperationFilter> filters, OutputChannel outputChannel) {
		this.filters = filters;
		this.msg = new Message(header);
		this.outputChannel = outputChannel;
		
		complete = false;
		
		for (OperationFilter filter : filters) {
			logger.debug("Filter " + filter.part);
			if (filter.interest.append() || filter.interest.full()) {
				needsBody = true;
			}
		}
		logger.debug(header.toString());
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

	public Message forceCompletion(Operation op) {
		complete = true;
		
		return filter(op);
	}
	
	private void checkCompletion() {
		//TODO: Implement proper logic
		complete = msg.getHeader() instanceof RequestHeader;
	}

	public Message append(Operation op, ByteBuffer buff) {
		
		if (!needsBody) {
			outputChannel.queueOutput(buff);
		} else {
		
			msg.appendToBody(buff);
			for (OperationFilter filter : filters) {
				
				if (filter.interest.append()) {
					Message result = filter.part.append(op, msg, buff);
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

	public Message filter(Operation op) {
		
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
		
		for (ByteBuffer buff : msg.getBody()) {
			outputChannel.queueOutput(buff);
		}

		return null;
	}

	private Message writeHeader() {
		
		try {
			outputChannel.queueOutput(ByteBuffer.wrap(msg.getHeader().toString().getBytes("ascii")));
		} catch (UnsupportedEncodingException e) {
			// If this happens, the world is screwed
			logger.error("Failed to encode header", e);
			
			//TODO: Return a 500 error
			return null;
		}
		
		return null;
	}
	
	public boolean isMessageComplete() {
		return complete;
	}

}
