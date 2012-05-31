package ar.edu.itba.pdc.duta.http.model;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;

public class Message {

	private MessageHeader header;
	
	private List<DataBuffer> body;

	public Message(MessageHeader header) {
		super();
		this.header = header;
		this.body = new ArrayList<DataBuffer>();
	}
	
	public void appendToBody(DataBuffer buff) {
		body.add(buff);
	}

	public MessageHeader getHeader() {
		return header;
	}

	public List<DataBuffer> getBody() {
		return body;
	}

	public void setBody(DataBuffer buffer) {
		body.clear();
		body.add(buffer);
	}
	
}
