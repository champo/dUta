package ar.edu.itba.pdc.duta.http;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class RequestParser extends MessageParser {

	private String method, requestURI, HTTPVersion;

	public RequestParser(ByteBuffer buffer) {
		super(buffer);
	}

	@Override
	protected MessageHeader createHeader(Map<String, String> fields) {

		return new RequestHeader(method, requestURI, HTTPVersion, fields);
	}

	@Override
	protected void setStartLine(String s) throws Exception{

		Scanner scan = new Scanner(s);
		
		try {
			method = scan.next();
			requestURI = scan.next();
			HTTPVersion = scan.next();
		} catch (NoSuchElementException e) {
			throw new Exception();
		}
		
		for (char c : method.toCharArray()) {
			if (!Grammar.isTokenCharacter(c)) {
				throw new Exception();
			}
		}

		if (scan.hasNext() || !Grammar.isRequestURI(requestURI) || !Grammar.isHTTPVersion(HTTPVersion)) {
			throw new Exception();
		}
	}
}
