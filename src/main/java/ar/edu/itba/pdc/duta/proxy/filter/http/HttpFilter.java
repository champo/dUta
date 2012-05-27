package ar.edu.itba.pdc.duta.proxy.filter.http;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.Interest;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class HttpFilter implements Filter {
	
	private static final Logger logger = Logger.getLogger(HttpFilter.class);

	@Override
	public FilterPart getRequestPart() {
		return new RequestPart();
	}

	@Override
	public FilterPart getResponsePart() {
		return new ResponsePart();
	}

	private static class ResponsePart extends FilterPart {
		
		@Override
		public Interest checkInterest(MessageHeader header) {
			return new Interest(true, false, false);
		}
		
		@Override
		public Message processHeader(Operation op, MessageHeader header) {
			header.setField("Via", "dUta");
			header.removeField("Connection");

			return null;
		}
		
	}
	
	private static class RequestPart extends FilterPart {

		@Override
		public Interest checkInterest(MessageHeader header) {
			return new Interest(true, false, false);
		}

		@Override
		public Message processHeader(Operation op, MessageHeader header) {
				
			// These are proxy specific headers that make origin servers go boom
			header.removeField("Proxy-Connection");
			header.removeField("Proxy-Authenticate");
			header.removeField("Proxy-Authorization");
			
			//FIXME: This shouldn't be needed!
			header.setField("Connection", "close");
			
			return null;
		}

	}
}
