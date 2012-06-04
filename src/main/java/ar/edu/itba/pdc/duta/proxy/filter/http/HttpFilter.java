package ar.edu.itba.pdc.duta.proxy.filter.http;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
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

			RequestHeader request = (RequestHeader) header;

			try {
				URL url = new URL(request.getRequestURI());
				if (header.getField("Host") == null) {
					header.setField("Host", url.getHost());
				} 

				String file = url.getFile();
				if (file.isEmpty()) {
					request.setRequestURI("/");
				} else {
					request.setRequestURI(file);
				}
			} catch (MalformedURLException e) {
				// This is cool, I think
			}

			return null;
		}
	}

	@Override
	public int getPriority() {

		return Integer.MAX_VALUE;
	}
}
