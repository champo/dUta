package ar.edu.itba.pdc.duta.http.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Message {

	private MessageHeader header;
	
	private List<ByteBuffer> body;

	public Message(MessageHeader header) {
		super();
		this.header = header;
		this.body = new ArrayList<ByteBuffer>();
	}
	
	public void appendToBody(ByteBuffer buff) {
		body.add(buff);
	}

	public MessageHeader getHeader() {
		return header;
	}

	public List<ByteBuffer> getBody() {
		return body;
	}
	
}
