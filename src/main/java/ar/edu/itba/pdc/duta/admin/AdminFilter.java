package ar.edu.itba.pdc.duta.admin;

import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.duta.admin.endpoint.Endpoint;
import ar.edu.itba.pdc.duta.admin.endpoint.stats.BytesEndpoint;
import ar.edu.itba.pdc.duta.admin.endpoint.stats.ChannelsEndpoint;
import ar.edu.itba.pdc.duta.admin.endpoint.stats.ClientBytesEndpoint;
import ar.edu.itba.pdc.duta.admin.endpoint.stats.ClientChannelsEndpoint;
import ar.edu.itba.pdc.duta.admin.endpoint.stats.ServerBytesEndpoint;
import ar.edu.itba.pdc.duta.admin.endpoint.stats.ServerChannelsEndpoint;
import ar.edu.itba.pdc.duta.http.MessageFactory;
import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.Interest;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class AdminFilter implements Filter {
	
	private static final Map<String, Endpoint> endpoints = new HashMap<String, Endpoint>();
	{
		endpoints.put("/stats/bytes", new BytesEndpoint());
		endpoints.put("/stats/bytes/clients", new ClientBytesEndpoint());
		endpoints.put("/stats/bytes/servers", new ServerBytesEndpoint());
		endpoints.put("/stats/channels", new ChannelsEndpoint());
		endpoints.put("/stats/channels/clients", new ClientChannelsEndpoint());
		endpoints.put("/stats/channels/servers", new ServerChannelsEndpoint());
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
			return new Interest(false, false, true);
		}
		
		@Override
		public Message filter(Operation op, Message msg) {
			
			RequestHeader header = (RequestHeader) msg.getHeader();
			
			String uri = header.getRequestURI().toLowerCase();
			Endpoint endpoint = endpoints.get(uri);
			if (endpoint == null) {
				return MessageFactory.build404();
			} else {
				//TODO: Check Accept headers and if there's a body, 400 out if so
				return endpoint.process(msg);
			}
		}
		
	}

	@Override
	public int getPriority() {

		return 0;
	}

}
