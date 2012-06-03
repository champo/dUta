package ar.edu.itba.pdc.duta.proxy.operation;

import java.io.IOException;

import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;

public class Http10Parser implements BodyParser {

	private Message msg;

	public Http10Parser(Message msg) {
		this.msg = msg;
	}

	@Override
	public int parse() throws IOException {
		DataBuffer body = msg.getBody();
		
		int old = body.getWriteIndex();
		body.consume();
		return body.getWriteIndex() - old;
	}

	@Override
	public boolean isComplete() {
		return false;
	}

}
