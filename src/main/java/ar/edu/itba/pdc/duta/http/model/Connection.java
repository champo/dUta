package ar.edu.itba.pdc.duta.http.model;

import ar.edu.itba.pdc.duta.http.Grammar;

public enum Connection {

	CLOSE("close"), KEEPALIVE("keep-alive");

	private String text;

	Connection(String text) {
		this.text = text;
	}

	public static Connection checkStatus(MessageHeader header) {
		
		String field = header.getField("Connection");
		if (field == null) {
			
			if (header.getHTTPVersion().equalsIgnoreCase(Grammar.HTTP11)) {
				return KEEPALIVE;
			}
			
		} else {
			
			if (field.equalsIgnoreCase(KEEPALIVE.text)) {
				return KEEPALIVE;
			}
		}

		return CLOSE;
	}

}
