package ar.edu.itba.pdc.duta.proxy.filter.http;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.http.Grammar;
import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.ResponseHeader;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.Interest;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class SizeFilter implements Filter {
	
	{
		Stats.registerFilterType(SizeFilter.class);
	}


	private final int maxSize;
	
	public SizeFilter(int maxSize) {
		super();
		this.maxSize = maxSize;
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
		
		private Integer size = null;

		@Override
		public Interest checkInterest(MessageHeader header) {
			
			boolean isSizeKnow = false;
			String field = header.getField("Content-Length");
			if (field != null) {
				
				try {
					size = Integer.valueOf(field.trim());
					isSizeKnow = true;
				} catch (NumberFormatException e) {
					// No biggie!
				}
				
			}
			
			return new Interest(true, !isSizeKnow, false);
		}
		@Override
		public Message processHeader(Operation op, MessageHeader header) {
			
			if (size != null && size >= maxSize) {
				return block();
			}
			
			return null;
		}
		
		@Override
		public Message bytesRecieved(Operation op, Message msg, long delta, long total) {
			
			if (total >= maxSize) {
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
