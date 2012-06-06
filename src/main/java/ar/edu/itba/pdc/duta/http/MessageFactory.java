package ar.edu.itba.pdc.duta.http;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.ResponseHeader;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;

public final class MessageFactory {
	
	private MessageFactory() {
	}
	
	public static Message build404() {
		return build(404, "Not Found", "");
	}
	
	public static Message build200(String body) {
		return build(200, "OK", body);
	}
	
	public static Message build(int code, String reason, String body) {
		return build(code, reason, body, "text/plain");
	}
	
	public static Message build(int code, String reason, String body, String contentType) {
		
		byte[] bytes;
		Map<String, String> fields = new HashMap<String, String>();
		
		fields.put("Via", "dUta");
		
		if (body != null) {
			
			try {
				bytes = body.getBytes("UTF8");
			} catch (UnsupportedEncodingException e) {
				// This shouldnt happen.
				return build500();
			}
			
			fields.put("Content-Length", String.valueOf(bytes.length));
			fields.put("Content-Type", contentType + "; encoding=UTF-8");
		} else {
			bytes = new byte[0];
		}
		
		MessageHeader header = new ResponseHeader(Grammar.HTTP11, code, reason, fields);
		Message message = new Message(header);
		
		DataBuffer buffer = new DataBuffer(bytes);
		message.setBody(buffer);
		buffer.release();
		
		return message;
	}

	public static Message build500() {
		return build(500, "Internal server error", "");
	}

	public static Message build400() {
		return build(400, "Bad Request", "");
	}

}
