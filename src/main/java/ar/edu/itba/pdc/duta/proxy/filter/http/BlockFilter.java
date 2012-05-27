package ar.edu.itba.pdc.duta.proxy.filter.http;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.duta.http.Grammar;
import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.ResponseHeader;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.Interest;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class BlockFilter implements Filter {

	@Override
	public FilterPart getRequestPart() {
		return new RequestPart();
	}

	@Override
	public FilterPart getResponsePart() {
		return null;
	}

	public static class RequestPart extends FilterPart {
		
		@Override
		public Interest checkInterest(MessageHeader header) {
			return new Interest(true, false, false);
		}
		
		@Override
		public Message processHeader(Operation op, MessageHeader header) {
			Map<String, String> fields = new HashMap<String, String>();
			
			fields.put("Date", new Date().toString());
			fields.put("Content-Type", "text/html");
			fields.put("Connection", "close");
			
			
			ResponseHeader headers = new ResponseHeader(Grammar.HTTP11, 404, "Not Found", fields);
			return new Message(headers);
		}		
	}
}
