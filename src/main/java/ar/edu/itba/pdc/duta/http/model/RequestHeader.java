package ar.edu.itba.pdc.duta.http.model;

import java.util.Map;


public class RequestHeader extends MessageHeader {

	private String method;
	private String requestURI;

	public RequestHeader(String method, String requestURI, String HTTPVersion, Map<String, String> fields) {

		super(fields);

		this.method = method;
		this.requestURI = requestURI;
		this.HTTPVersion = HTTPVersion;
	}

	public String getMethod() {
		return method;
	}

	public String getRequestURI() {
		return requestURI;
	}

	@Override
	protected String getStartLine() {
		return method + " " + requestURI + " " + HTTPVersion + "\r\n";
	}

	public void setRequestURI(String uri) {
		requestURI = uri;
	}

}
