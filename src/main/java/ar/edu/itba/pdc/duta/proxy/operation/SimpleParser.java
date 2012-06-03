package ar.edu.itba.pdc.duta.proxy.operation;

import java.io.IOException;

import ar.edu.itba.pdc.duta.http.model.Message;

public class SimpleParser implements BodyParser {

	private Message msg;

	public SimpleParser(Message msg) {
		super();
		this.msg = msg;
	}

	@Override
	public void parse() throws IOException {
		Integer length = getLength();
		if (length == null) {
			return;
		}
		
		msg.getBody().consume(length - msg.getBody().getWriteIndex());
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
