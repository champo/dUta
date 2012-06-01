package ar.edu.itba.pdc.duta.http.parser;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ar.edu.itba.pdc.duta.http.Grammar;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;

public class RequestParser extends MessageParser {

	private String method;
	private String requestURI;
	private String HTTPVersion;

	public RequestParser(DataBuffer buffer) {
		super(buffer);
	}

	@Override
	protected MessageHeader createHeader(Map<String, String> fields, Map<String, String> fieldNames) {

		return new RequestHeader(method, requestURI, HTTPVersion, fields, fieldNames);
	}

	@Override
	protected void setStartLine(String s) throws ParseException{

		Scanner scan = new Scanner(s);
		
		try {
			method = scan.next();
			requestURI = scan.next();
			HTTPVersion = scan.next();
		} catch (NoSuchElementException e) {
			throw new ParseException("Invalid start line: missing parameters");
		}
		
		for (char c : method.toCharArray()) {
			if (!Grammar.isTokenCharacter(c)) {
				throw new ParseException("Invalid method");
			}
		}

		if (scan.hasNext()) {
			throw new ParseException("Invalid start line: extra parameters");
		}
			
		if (!Grammar.isHTTPVersion(HTTPVersion)) {
			throw new ParseException("Invalid HTTP version");
		}
	}
}
