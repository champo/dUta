package ar.edu.itba.pdc.duta.proxy.filter.http;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.http.MessageFactory;
import ar.edu.itba.pdc.duta.http.model.MediaType;
import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.Interest;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class MediaTypeFilter implements Filter {
	
	{
		Stats.registerFilterType(MediaTypeFilter.class);
	}


	private final MediaType mediaType;
	
	public MediaTypeFilter(String mediaType) {

		this.mediaType = MediaType.valueOf(mediaType);
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

			if (mediaType.isCompatible(MediaType.valueOf(header.getField("Content-Type")))) {

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
