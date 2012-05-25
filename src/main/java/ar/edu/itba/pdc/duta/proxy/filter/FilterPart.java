package ar.edu.itba.pdc.duta.proxy.filter;

import java.nio.ByteBuffer;

import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.ResponseHeader;

public interface FilterPart {
	
	public Interest checkInterest(MessageHeader header);

	/**
	 * 
	 * @param header
	 * 
	 * @return If the request is to be aborted, return a ResponseHeader
	 */
	public ResponseHeader preProcessHeader(MessageHeader header);
	
	public boolean process(ByteBuffer buff);
	
	public boolean filter(ByteBuffer buff);
	
	/**
	 * 
	 * @param header
	 * 
	 * @return If the request is to be aborted, return a ResponseHeader
	 */
	public ResponseHeader postProcessHeader(MessageHeader header);
}
