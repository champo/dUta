package ar.edu.itba.pdc.duta.proxy.operation;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.http.parser.ParseException;
import ar.edu.itba.pdc.duta.http.parser.ResponseParser;
import ar.edu.itba.pdc.duta.proxy.RequestChannelHandler;
import ar.edu.itba.pdc.duta.proxy.ResponseChannelHandler;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.http.HttpFilter;

public class Operation {
	
	private static final Logger logger = Logger.getLogger(Operation.class);

	private RequestChannelHandler requestChannel;

	private ResponseChannelHandler responseChannel;
	
	private List<Filter> filters;
	
	private boolean closed = false;

	private FilterChain requestChain;

	private FilterChain responseChain;

	public Operation(RequestChannelHandler requestChannelHandler) {
		requestChannel = requestChannelHandler;
	}
	
	public void setRequestHeader(RequestHeader header, ByteBuffer buff) {
		logger.debug("Setting request header");
		buildFilterList(header);
		
		List<OperationFilter> requestFilters = new ArrayList<OperationFilter>();
		
		for (Filter filter : filters) {
			FilterPart requestPart = filter.getRequestPart();
			if (requestPart != null) {
				requestFilters.add(new OperationFilter(requestPart, requestPart.checkInterest(header)));
			}
		}
		
		logger.debug("Filter list for request: " + requestFilters);

		InetSocketAddress address = extractAddress(header);
		if (address == null) {
			//TODO: Return a 500 error
			throw new RuntimeException("Fuckity fuck");
		}
		
		logger.debug("Destination address: " + address);
		
		ChannelProxy proxy = new ChannelProxy(address, this);
		requestChain = new FilterChain(header, requestFilters, proxy);
		
		Message res = requestChain.processHeader(this);
		if (res != null) {
			writeMessage(res);
			return;
		}
		
		if (!isRequestComplete()) {
			if (buff.hasRemaining()) {
				buff.compact();
				buff.flip();
				addRequestData(buff);
			}
		}
		
	}

	private void writeMessage(Message res) {
		
		logger.debug("Got a response message from a filter. Headers are: " + res.getHeader());
		
		try {
			requestChannel.queueOutput(ByteBuffer.wrap(res.getHeader().toString().getBytes("ascii")));
		} catch (UnsupportedEncodingException e) {
			// If this happens, the world is screwed
			logger.error("Failed to encode header", e);
			
			//TODO: Return a 500 error
			return;
		}
		
		for (ByteBuffer buffer : res.getBody()) {
			requestChannel.queueOutput(buffer);
		}
		
		close();
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
				return null;
			}
		} else {				
			return new InetSocketAddress(host, 80);
		}
	}


	private void buildFilterList(RequestHeader header) {
		//FIXME: Get this from somewhere else
		filters = new ArrayList<Filter>();
		filters.add(new HttpFilter());
		//filters.add(new BlockFilter());
	}

	
	public void close() {
		
		//FIXME: Handle soft close & force close (soft is keep connection, force is drop connection)
		if (responseChannel != null) {
			responseChannel.close();
		}
		
		if (responseChain != null) {
			Message res = responseChain.forceCompletion(this);
			if (res != null) {
				writeMessage(res);
			}
		}
		
		if (requestChannel != null) {
			requestChannel.close();
		}
		
		closed = true;
	}
	
	public void addRequestData(ByteBuffer inputBuffer) {
		Message res = requestChain.append(this, inputBuffer);
		if (res != null) {
			writeMessage(res);
		}
	}

	public boolean isRequestComplete() {
		return closed || requestChain.isMessageComplete();
	}
	
	public void addResponseData(ByteBuffer buffer) {
		
		if (responseChain == null) {
		
			logger.debug("Buffer content " + new String(buffer.array()));
			
			logger.debug("Pre parse buffer " + buffer);
			ResponseParser parser = new ResponseParser(buffer);
			logger.debug("Pre parse buffer " + buffer);
			try {
				parser.parse();
			} catch (ParseException e) {
				logger.error("Failed to parse response header", e);
				
				close();
				return;
			}
			
			MessageHeader header = parser.getHeader();
			List<OperationFilter> responseFilters = new ArrayList<OperationFilter>();
			
			for (Filter filter : filters) {
				FilterPart responsePart = filter.getResponsePart();
				if (responsePart != null) {
					responseFilters.add(new OperationFilter(responsePart, responsePart.checkInterest(header)));
				}
			}

			responseChain = new FilterChain(header, responseFilters, requestChannel);
			Message res = responseChain.processHeader(this);
			if (res != null) {
				writeMessage(res);
			}
			
			if (!responseChain.isMessageComplete() && buffer.hasRemaining()) {
				buffer.compact();
				buffer.flip();
				logger.debug("Buffer content (after compact) " + new String(buffer.array()));
			} else {
				return;
			}
			
		}
		
		Message res = responseChain.append(this, buffer);
		if (res != null) {
			writeMessage(res);
		}
	}
}
