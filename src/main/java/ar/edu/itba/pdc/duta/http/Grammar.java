package ar.edu.itba.pdc.duta.http;

import java.util.regex.Pattern;

public class Grammar {
	
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

	public static boolean isRequestURI(String requestURI) {

		// TODO
		return true;
	}
}
