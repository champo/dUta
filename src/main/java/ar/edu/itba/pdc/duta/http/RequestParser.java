package ar.edu.itba.pdc.duta.http;

import java.nio.ByteBuffer;

public class RequestParser extends MessageParser {

	public RequestParser(ByteBuffer buffer) {
		
		super(buffer);
		header = new RequestHeader();
	}

}
