package ar.edu.itba.pdc.duta.http;

import java.util.regex.Pattern;

public class Grammar {

	public static final String HTTP11 = "HTTP/1.1";

	/* 
	 * Warning: this array should be ordered, and its members conforming to
	 * org.apache.commons.lang3.text.WordUtils.capitalizeFully( member, '-')
	 */
	public static final String[] singleHeadersBlacklist = {

		"Age",
		"Content-Length",
		"Content-Location",
		"Content-Md5",
		"Content-Range",
		"Content-Type",
		"Date",
		"Etag",
		"Expires",
		"From",
		"Host",
		"If-Modified-Since",
		"If-Range",
		"If-Unmodified-Since",
		"Last-Modified",
		"Location",
		"Max-Forwards",
		"Referer",
		"Retry-After",
	};
	
	public static boolean isControlCharacter(char c) {
		
		return c < '\u0020' || c == '\u007f';
	}

	public static boolean isTokenCharacter(char c) {
	
		if (isControlCharacter(c)) {
			return false;
		}
		
		if (c == '(' || c == ')' || c == '<' || c == '>' || c == '@' ||
			c == ',' || c == ';' || c == ':' || c == '\\' || c == '"' ||
			c == '/' || c == '[' || c == ']' || c == '?' || c == '=' ||
			c == '{' || c == '}' || c == ' ' || c == '\t' ) {
			return false;
		}

		return true;
	}
	
	public static boolean isHTTPVersion(String s) {
		
		return Pattern.matches("HTTP/[0-9]+\\.[0-9]+", s);
	}
	
	public static boolean isLWS(char c) {
		return c == '\t' || c == ' ';
	}
	
}
