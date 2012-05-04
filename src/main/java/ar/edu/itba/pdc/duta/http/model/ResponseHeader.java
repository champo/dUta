package ar.edu.itba.pdc.duta.http.model;

import java.util.Map;


public class ResponseHeader extends MessageHeader {

	private String HTTPVersion;
	private String reasonPhrase;
	private int statusCode;

	public ResponseHeader(String HTTPVersion, int statusCode, String reasonPhrase, Map<String, String> fields) {

		super(fields);
	
		this.HTTPVersion = HTTPVersion;
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
	}

	public String getHTTPVersion() {
		return HTTPVersion;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}

	public int getStatusCode() {
		return statusCode;
	}

	@Override
	protected String getStartLine() {
		return HTTPVersion + " " + statusCode + " " + reasonPhrase + "\r\n";
	}

}
