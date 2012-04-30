package ar.edu.itba.pdc.duta.http;

import java.util.Scanner;

public class RequestHeader extends MessageHeader {

	private String method, requestURI, HTTPVersion;

	@Override
	void setStartLine(String s) throws Exception {

		Scanner scan = new Scanner(s);
		
		if (!scan.hasNext()) {
			throw new Exception();
		}
		method = scan.next();
		
		for (char c : method.toCharArray()) {
			if (!Grammar.isTokenCharacter(c)) {
				throw new Exception();
			}
		}

		if (!scan.hasNext()) {
			throw new Exception();
		}
		requestURI = scan.next();
		// TODO: validate URI
		
		if (!scan.hasNext()) {
			throw new Exception();
		}
		HTTPVersion = scan.next();
		
		if (!Grammar.isHTTPVersion(HTTPVersion)) {
			throw new Exception();
		}
		if (scan.hasNext()) {
			throw new Exception();
		}
	}

	public String getMethod() {
		return method;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public String getHTTPVersion() {
		return HTTPVersion;
	}

}
