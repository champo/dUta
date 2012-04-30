package ar.edu.itba.pdc.duta.http;

import java.util.Scanner;

public class ResponseHeader extends MessageHeader {

	private String HTTPVersion, reasonPhrase;
	private int statusCode;

	@Override
	void setStartLine(String s) throws Exception {

		Scanner scan = new Scanner(s);
		
		if (!scan.hasNext()) {
			throw new Exception();
		}
		HTTPVersion = scan.next();
		
		if (!Grammar.isHTTPVersion(HTTPVersion)) {
			throw new Exception();
		}
		if (!scan.hasNextInt()) {
			throw new Exception();
		}
		statusCode = scan.nextInt();
		
		if (statusCode < 100 || statusCode > 999) {
			throw new Exception();
		}
		if (!scan.hasNext()) {
			throw new Exception();
		}
		reasonPhrase = scan.nextLine();
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

}
