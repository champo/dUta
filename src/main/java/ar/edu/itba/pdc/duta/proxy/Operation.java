package ar.edu.itba.pdc.duta.proxy;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.http.model.ResponseHeader;
import ar.edu.itba.pdc.duta.net.Server;
import ar.edu.itba.pdc.duta.net.Server.Stats;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.Interest;
import ar.edu.itba.pdc.duta.proxy.filter.http.HttpFilter;

public class Operation {
	
	private static final Logger logger = Logger.getLogger(Operation.class);
	
	private RequestChannelHandler requestChannel;

	private RequestHeader requestHeader;
	
	private List<OperationFilter> requestFilters;
	
	private ResponseChannelHandler responseChannel;
	
	private ResponseHeader responseHeader;
	
	private List<OperationFilter> responseFilters;

	public Operation(RequestChannelHandler requestChannelHandler) {
		requestChannel = requestChannelHandler;
	}
	
	public void setRequestHeader(RequestHeader header) {
		requestHeader = header;
		buildFilterList();

		for (OperationFilter filter : requestFilters) {
			filter.interest = filter.part.checkInterest(header);
			
			if (filter.interest.preProcess()) {
				ResponseHeader result = filter.part.preProcessHeader(header);
				if (result != null) {
					// Jump ship! It's going down!
				}
			}
		}

		if (isRequestComplete()) {
			postProcessRequest();
		}
	}
	
	private void postProcessRequest() {
		
		for (OperationFilter filter : requestFilters) {
			
			if (filter.interest.postProcess()) {
				ResponseHeader result = filter.part.postProcessHeader(requestHeader);
				if (result != null) {
					// Jump ship! It's going down!
				}
			}
		}
		
		logger.debug("Got request, asking for responsee");
		
		ResponseChannelHandler response = new ResponseChannelHandler(this);

		try {
			InetSocketAddress remote = extractAddress(requestHeader);
			if (remote == null) {
				return;
			}
			
			Server.newConnection(remote, response);
			Stats.newOutbound();
		} catch (Exception e) {
			logger.error("Caught exception trying to create outbound connection.", e);
			close();
			return;
		}

		String request = requestHeader.toString();
		response.queueOutput(ByteBuffer.wrap(request.getBytes()));
	}

	private void buildFilterList() {
		requestFilters = new ArrayList<OperationFilter>();
		responseFilters = new ArrayList<OperationFilter>();
		
		//FIXME: Get this from somewhere else
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new HttpFilter());
		
		for (Filter filter : filters) {
			FilterPart requestPart = filter.getRequestPart();
			if (requestPart != null) {
				requestFilters.add(new OperationFilter(requestPart));
			}
			
			FilterPart responsePart = filter.getResponsePart();
			if (responsePart != null) {
				responseFilters.add(new OperationFilter(responsePart));
			}
		}
	}
	
	private InetSocketAddress extractAddress(RequestHeader header) {
		
		String host = header.getField("Host");
		
		if (host.contains(":")) {
			String[] split = host.split(":");
			
			try {
				return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
			} catch (NumberFormatException e) {
				// Force close!
				logger.error("Got an invalid port number in the Host header", e);
				close();
				return null;
			}
		} else {				
			return new InetSocketAddress(host, 80);
		}
	}
	
	public void close() {
		
		//FIXME: Handle soft close & force close (soft is keep connection, force is drop connection)
		
		if (responseChannel != null) {
			responseChannel.close();
		}
		
		if (requestChannel != null) {
			requestChannel.close();
		}
	}
	
	public boolean addRequestData(ByteBuffer inputBuffer) {
		//TODO: Implement me
		return false;
	}

	public boolean isRequestComplete() {
		return true;
	}
	
	private static class OperationFilter {
		
		private FilterPart part;
		
		private Interest interest;
		
		public OperationFilter(FilterPart part) {
			super();
			this.part = part;
		}

		public FilterPart getPart() {
			return part;
		}
		
		public void setInterest(Interest interest) {
			this.interest = interest;
		}
		
		public Interest getInterest() {
			return interest;
		}
	}

	public void queueOutput(ByteBuffer buffer) {
		requestChannel.queueOutput(buffer);
	}
}
