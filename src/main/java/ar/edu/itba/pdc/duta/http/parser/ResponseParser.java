package ar.edu.itba.pdc.duta.http.parser;

import java.nio.ByteBuffer;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ar.edu.itba.pdc.duta.http.Grammar;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.http.model.ResponseHeader;

public class ResponseParser extends MessageParser {

	private String HTTPVersion;
	private String reasonPhrase;
	private int statusCode;

	public ResponseParser(ByteBuffer buffer) {
		super(buffer);
	}

	@Override
	protected MessageHeader createHeader(Map<String, String> fields) {

		return new ResponseHeader(HTTPVersion, statusCode, reasonPhrase, fields);
	}

	@Override
	protected void setStartLine(String s) throws ParseException{

		Scanner scan = new Scanner(s);
		
		try {
			HTTPVersion = scan.next();
			statusCode = scan.nextInt();
		} catch (InputMismatchException e) {
			throw new ParseException("Invalid status code");
		} catch (NoSuchElementException e) {
			throw new ParseException("Invalid start line: missing parameters");
		}

		try {
			reasonPhrase = scan.nextLine().trim();
		} catch (NoSuchElementException e) {
			reasonPhrase = "";
		}

		if (!Grammar.isHTTPVersion(HTTPVersion)){
			throw new ParseException("Invalid HTTP version");
		}
		
		if (statusCode < 100 || statusCode > 999) {
			throw new ParseException("Invalid status code");
		}
	}
}
