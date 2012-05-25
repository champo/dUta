package ar.edu.itba.pdc.duta.proxy.filter.http;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.ResponseHeader;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.Interest;

public class HttpFilter implements Filter {
	
	private static final Logger logger = Logger.getLogger(HttpFilter.class);

	@Override
	public FilterPart getRequestPart() {
		return new RequestPart();
	}

	@Override
	public FilterPart getResponsePart() {
		return null;
	}

	
	private class RequestPart implements FilterPart {

		@Override
		public Interest checkInterest(MessageHeader header) {
			return new Interest(false, true, false, false);
		}

		@Override
		public ResponseHeader preProcessHeader(MessageHeader header) {
			return null;
		}

		@Override
		public boolean process(ByteBuffer buff) {
			return false;
		}

		@Override
		public boolean filter(ByteBuffer buff) {
			return false;
		}

		@Override
		public ResponseHeader postProcessHeader(MessageHeader header) {
			
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
