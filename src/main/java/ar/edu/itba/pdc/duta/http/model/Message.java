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

	public boolean isComplete() {

		String encoding = header.getField("Transfer-Encoding");
		Integer len = getLength();

		if (len != null) {
			if (body == null) {
				return len == 0;
			} else {
				return len <= body.getWriteIndex();
			}
		} else if (encoding == null || encoding.isEmpty() || "identity".equals(encoding)) {
			return true;
		} else {
			// TODO: Chuncked sucks
		}

		// Just for the lulz
		return false;
	}

	public Integer getLength() {
		String length = header.getField("Content-Length");

		try {
			return Integer.parseInt(length);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
