package ar.edu.itba.pdc.duta.proxy.operation;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.http.model.ResponseHeader;

public class RequestLog {
	
	private static final Logger logger = Logger.getLogger("accessLog");
	
	private RequestHeader request;

	public RequestLog(RequestHeader request) {
		super();
		this.request = request;
	}
	
	public boolean logResponse(MessageHeader header) {
		
		if (header != null && !(header instanceof ResponseHeader)) {
			return false;
		}
		
		StringBuilder msg = new StringBuilder();
		
		msg.append(request.getMethod());
		msg.append(' ');
		msg.append(request.getRequestURI());
		
		String host = request.getField("Host");
		if (host != null) {
			msg.append(" (Host: ");
			msg.append(host);
			msg.append(')');
		}
		
		
		if (header == null) {
			msg.append(" -- aborted");
		} else {
			
			ResponseHeader response = (ResponseHeader) header;
			
			msg.append(' ');
			msg.append(response.getStatusCode());
		}
		
		logger.info(msg.toString());
		
		return true;
	}

}
