package ar.edu.itba.pdc.duta.http.parser;

import java.io.IOException;

import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;

public class Http10Parser implements BodyParser {

	private Message msg;
	
	private boolean done = false;

	public Http10Parser(Message msg) {
		this.msg = msg;
	}

	@Override
	public int parse() throws IOException {
		DataBuffer body = msg.getBody();
		
		int old = body.getWriteIndex();
		try {
			body.consume(Short.MAX_VALUE);
		} catch (IOException e) {
			done = true;
			return 0;
		}
		
		return body.getWriteIndex() - old;
	}

	@Override
	public boolean isComplete() {
		return done;
	}

}
