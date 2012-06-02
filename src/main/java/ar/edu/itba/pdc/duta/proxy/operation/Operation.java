package ar.edu.itba.pdc.duta.proxy.operation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.http.model.Connection;
import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.http.parser.ParseException;
import ar.edu.itba.pdc.duta.http.parser.ResponseParser;
import ar.edu.itba.pdc.duta.net.Server;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;
import ar.edu.itba.pdc.duta.net.buffer.FixedDataBuffer;
import ar.edu.itba.pdc.duta.net.buffer.WrappedDataBuffer;
import ar.edu.itba.pdc.duta.proxy.RequestChannelHandler;
import ar.edu.itba.pdc.duta.proxy.ResponseChannelHandler;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.http.HttpFilter;
import ar.edu.itba.pdc.duta.proxy.filter.http.URIFilter;

public class Operation {
	
	private static final Logger logger = Logger.getLogger(Operation.class);

	private RequestChannelHandler requestChannel;
	
	private List<Filter> filters;
	
	private boolean closed = false;

	private FilterChain requestChain;

	private FilterChain responseChain;

	private boolean closeResponse = false;
	
	private boolean closeRequest = false;

	private ChannelProxy responseProxy;
	
	public Operation(RequestChannelHandler requestChannelHandler) {
		requestChannel = requestChannelHandler;
	}
	
	public void setRequestHeader(RequestHeader header) {
		
		closeRequest = Connection.checkStatus(header) == Connection.CLOSE;
		
		buildFilterList(header);
		
		List<OperationFilter> requestFilters = new ArrayList<OperationFilter>();
		
		for (Filter filter : filters) {
			FilterPart requestPart = filter.getRequestPart();
			if (requestPart != null) {
				requestFilters.add(new OperationFilter(requestPart, requestPart.checkInterest(header)));
			}
		}
		
		InetSocketAddress address = extractAddress(header);
		if (address == null) {
			//TODO: Return a 500 error
			throw new RuntimeException("Fuckity fuck");
		}
		
		logger.debug("Destination address: " + address);
		
		responseProxy = new ChannelProxy(address, this);
		requestChain = new FilterChain(header, requestFilters, responseProxy);
		
		Message res = requestChain.processHeader(this);
		if (res != null) {
			writeMessage(res);
			return;
		}
	}

	private void writeMessage(Message res) {
		
		logger.debug("Got a response message from a filter. Headers are: " + res.getHeader());
		
		try {
			requestChannel.queueOutput(new FixedDataBuffer(res.getHeader().toString().getBytes("ascii")));
		} catch (UnsupportedEncodingException e) {
			// If this happens, the world is screwed
			logger.error("Failed to encode header", e);
			
			//TODO: Return a 500 error
			return;
		}
		
		for (DataBuffer buffer : res.getBody()) {
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
		//filters.add(new L33tFilter());
		//filters.add(new BlockFilter());
		//filters.add(new MediaTypeFilter("image/jpeg"));
		//filters.add(new URIFilter("www.google.com.ar"));
		
		
	}
	
	public void abort() {
		
		if (responseProxy != null && responseProxy.getChannel() != null) {
			ResponseChannelHandler handler = responseProxy.getChannel();
			
			handler.setOp(null);
			handler.close();
		}
		
		if (requestChannel != null) {
			requestChannel.operationComplete();
			requestChannel.close();
		}
		
		//TODO: Make sure all buffers are cleaned up by asking the chains to do so
	}

	public void close() {
		
		if (responseProxy != null && responseProxy.getChannel() != null) {
			if (closeResponse) {
				responseProxy.getChannel().close();
			} else {
				Server.getConnectionPool().registerConnection(responseProxy.getChannel());
			}
		}
		responseProxy = null;
		
		if (responseChain != null && !responseChain.isMessageComplete()) {
			Message res = responseChain.forceCompletion(this);
			if (res != null) {
				writeMessage(res);
			}
		}
		responseChain = null;
		
		if (requestChannel != null) {
			
			requestChannel.operationComplete();
			if (closeRequest) { 
				requestChannel.close();
			}
			
		}
		requestChannel = null;
		
		closed = true;
	}
	
	public void addRequestData(DataBuffer buffer) {
		Message res = requestChain.append(this, buffer);
		if (res != null) {
			writeMessage(res);
		}
	}

	public boolean isRequestComplete() {
		return closed || requestChain.isMessageComplete();
	}
	
	public void addResponseData(DataBuffer buffer) {
		
		if (responseChain == null) {
		
			ResponseParser parser = new ResponseParser();
			try {
				parser.parse(buffer);
			} catch (ParseException e) {
				logger.error("Failed to parse response header", e);
				
				//TODO: 500 out
				close();
				requestChannel.close();
				
				return;
			} catch (IOException e) {
				logger.error("Failed to parse response header", e);
				
				//TODO: 500 out
				close();
				requestChannel.close();
				
				return;
			}
			
			MessageHeader header = parser.getHeader();
			closeResponse = Connection.checkStatus(header) == Connection.CLOSE;
			
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
			
			if (buffer.hasReadableBytes()) {
				buffer = new WrappedDataBuffer(buffer, buffer.getReadIndex(), buffer.remaining());
			} else if (responseChain.isMessageComplete()) {
				close();
				return;
			}
			
		}
		
		Message res = responseChain.append(this, buffer);
		if (res != null) {
			writeMessage(res);
		} else if (responseChain.isMessageComplete()) {
			close();
		}
		
	}

	public DataBuffer getRequestBuffer() {
		
		if (requestChain == null) {
			return new FixedDataBuffer(4096);
		} else {
			return requestChain.getBuffer();
		}
	}
	
	public DataBuffer getResponseBuffer() {
		
		if (responseChain == null) {
			return new FixedDataBuffer(4096);
		} else {
			return responseChain.getBuffer();
		}
	}
}
