package ar.edu.itba.pdc.duta.http;

import java.nio.ByteBuffer;

public class ResponseParser extends MessageParser {

	public ResponseParser(ByteBuffer buffer) {
		
		super(buffer);
		header = new ResponseHeader();
	}

}
