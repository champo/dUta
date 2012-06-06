package ar.edu.itba.pdc.duta.http.parser;

import java.io.IOException;

import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;

public class SimpleParser implements BodyParser {

	private Message msg;

	public SimpleParser(Message msg) {
		super();
		this.msg = msg;
	}

	@Override
	public int parse() throws IOException {
		
		Integer length = getLength();
		if (length == null) {
			return 0;
		}
		
		DataBuffer body = msg.getBody();
		
		int pos = body.getWriteIndex();
		body.consume(length - pos);
		
		return body.getWriteIndex() - pos;
	}

	@Override
	public boolean isComplete() {
		
		Integer length = getLength();
		if (length == null) {
			return true;
		}
		
		return length <= msg.getBody().getWriteIndex();
	}
	
	private Integer getLength() {
		String length = msg.getHeader().getField("Content-Length");

		try {
			return Integer.parseInt(length);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
}
