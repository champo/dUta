package ar.edu.itba.pdc.duta.proxy.filter.http;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.http.MessageFactory;
import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.Interest;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class BlockFilter implements Filter {
	
	{
		Stats.registerFilterType(BlockFilter.class);
	}

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

			return MessageFactory.build404();
		}
	}

	@Override
	public int getPriority() {

		return 16;
	}
}
