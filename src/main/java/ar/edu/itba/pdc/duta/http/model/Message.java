package ar.edu.itba.pdc.duta.http.model;

import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;

public class Message {

	private MessageHeader header;

	private DataBuffer body;

	public Message(MessageHeader header) {
		super();
		this.header = header;
	}

	public MessageHeader getHeader() {
		return header;
	}

	public DataBuffer getBody() {
		return body;
	}

	public void setBody(DataBuffer buffer) {
		
		if (this.body != null) {
			this.body.release();
		}

		if (buffer != null) {
			buffer.retain();
		}
		
		this.body = buffer;
	}

	public int getCurrentBodySize() {
		return body.getWriteIndex();
	}

}
