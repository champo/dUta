package ar.edu.itba.pdc.duta.http;

import java.nio.ByteBuffer;


public class Test {

	public static void main(String[] args) throws Exception {

		String request =
				"GET / HTTP/1.1\n" +
				"User-Agent: curl/7.24.0 (x86_64-apple-darwin11.2.0) libcurl/7.24.0 OpenSSL/1.0.0h zlib/1.2.6 libidn/1.22\n" +
				"Host: www.google.com.ar\n" +
				"Accept: */*\n" +
				"\n";
			
		String response =
			"HTTP/1.1 200 OK\n" +
			"Date: Mon, 30 Apr 2012 08:04:46 GMT\n" +
			"Expires: -1\n" +
			"Cache-Control: private, max-age=0\n" +
			"Content-Type: text/html; charset=ISO-8859-1\n" +
			"Set-Cookie: PREF=ID=aea7b5fa435a0e78:FF=0:TM=1335773086:LM=1335773086:S=zYQArNYhvlUT7Oyx; expires=Wed, 30-Apr-2014 08:04:46 GMT; path=/; domain=.google.com.ar\n" +
			"Set-Cookie: NID=59=nG4G5-Z76RKty9gHIUVNbnXlTD7g2gF7IANn9v4iCh2RkXyHb4b75p7ehsV4rBuxZoo_uHcnda9nM9Vz1UvYCBPUTMurrqJ_BKNsDU7m7S0cpV74eUbBsXh59hpgz6HA; expires=Tue, 30-Oct-2012 08:04:46 GMT; path=/; domain=.google.com.ar; HttpOnly\n" +
			"P3P: CP=\"This is not a P3P policy! See http://www.google.com/support/accounts/bin/answer.py?hl=en&answer=151657 for more info.\"\n" +
			"Server: gws\n" +
			"X-XSS-Protection: 1; mode=block\n" +
			"X-Frame-Options: SAMEORIGIN\n" +
			"Transfer-Encoding: chunked\n" +
			"\n" +
			"blablabla";

		MessageParser parser;
		
		parser = new RequestParser(ByteBuffer.wrap(request.getBytes("US-ASCII")));
		while (!parser.parse());
		
		System.out.println(parser.getHeader());
		
		parser = new ResponseParser(ByteBuffer.wrap(response.getBytes("US-ASCII")));
		while (!parser.parse());

		System.out.println(parser.getHeader());
	}
}
