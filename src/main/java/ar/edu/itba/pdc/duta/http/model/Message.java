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
		this.body = buffer;
	}

	public int getCurrentBodySize() {
		return body.getWriteIndex();
	}

	public boolean isComplete() {

		String encoding = header.getField("Transfer-Encoding");
		int len = getLength();

		if (len != -1) {
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

	private int getLength() {
		String length = header.getField("Content-Length");

		int len = 0;
		try {
			len = Integer.parseInt(length);
		} catch (NumberFormatException e) {
			len = -1;
		}
		return len;
	}

}
