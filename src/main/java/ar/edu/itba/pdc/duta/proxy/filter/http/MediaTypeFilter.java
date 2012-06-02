package ar.edu.itba.pdc.duta.proxy.filter.http;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.http.Grammar;
import ar.edu.itba.pdc.duta.http.model.MediaType;
import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.ResponseHeader;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.Interest;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class MediaTypeFilter implements Filter {
	
	{
		Stats.registerFilterType(MediaTypeFilter.class);
	}


	private final String mediaType;
	
	public MediaTypeFilter(String mediaType) {
		super();
		this.mediaType = mediaType;
	}

	@Override
	public FilterPart getRequestPart() {
		return null;
	}

	@Override
	public FilterPart getResponsePart() {
		return new ResponsePart();
	}
	
	public class ResponsePart extends FilterPart {

		@Override
		public Interest checkInterest(MessageHeader header) {
			return new Interest(true, false, false);
		}
		
		@Override
		public Message processHeader(Operation op, MessageHeader header) {

			MediaType contentType = new MediaType(header.getField("Content-Type"));
			if (contentType.getType().equalsIgnoreCase(mediaType)) {
				return block();
			}
			
			return null;
		}
		
		private Message block() {
			Map<String, String> fields = new HashMap<String, String>();
			
			fields.put("Date", new Date().toString());
			fields.put("Content-Length", "0");
			
			ResponseHeader headers = new ResponseHeader(Grammar.HTTP11, 404, "Not Found", fields);
			return new Message(headers);
		}
		
	}

}
