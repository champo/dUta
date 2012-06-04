package ar.edu.itba.pdc.duta.proxy.filter.http;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.http.Grammar;
import ar.edu.itba.pdc.duta.http.MessageFactory;
import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.http.model.ResponseHeader;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.Interest;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class URIFilter implements Filter {

	{
		Stats.registerFilterType(URIFilter.class);
	}

	private final Pattern pattern; 

	public URIFilter(String uriBlocked) {

		pattern = Pattern.compile(uriBlocked);
	}

	@Override
	public FilterPart getRequestPart() {

		return new RequestPart();
	}

	@Override
	public FilterPart getResponsePart() {

		return null;
	}
	
	private class RequestPart extends FilterPart {

		@Override
		public Interest checkInterest(MessageHeader header) {
			return new Interest(true, false, false);
		}

		@Override
		public Message processHeader(Operation op, MessageHeader header) {

			String url = op.getServerProxy().getAddress().getHostName() + ((RequestHeader) header).getRequestURI();

			if (pattern.matcher(url).matches()) {

				return MessageFactory.build404();
			}

			return null;
		}
	}

	@Override
	public int getPriority() {

		return 16;
	}
}
