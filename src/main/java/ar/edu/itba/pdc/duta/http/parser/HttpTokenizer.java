package ar.edu.itba.pdc.duta.http.parser;

import ar.edu.itba.pdc.duta.http.Grammar;

public class HttpTokenizer {
	
	private char[] s;
	
	private int i;
	
	public HttpTokenizer(String s) {
		this.s = s.toCharArray();
	}
	
	private void consumeLWS() {
		
		for (; i < s.length; i++) {
			
			if (!Grammar.isLWS(s[i])) {
				break;
			}
		}
	}

	public String getNextToken() {
		
		consumeLWS();
		
		StringBuilder token = new StringBuilder();
		
		for (; i < s.length; i++) {
			
			if (Grammar.isTokenCharacter(s[i])) {
				token.append(s[i]);
			} else {
				break;
			}
		}
		
		if (token.length() == 0) {
			return null;
		}
		
		return token.toString();
	}
	
	public String getValue() {
		
		if (consumeUntil('"')) {
			i--;
			return getQuotedString();
		} else {
			return getNextToken();
		}
		
	}
	
	public String getQuotedString() {
		
		if (!consumeUntil('"')) {
			return null;
		}
		
		StringBuilder str = new StringBuilder();
		boolean escapeNext = false;
		
		for (; i < s.length; i++) {

			if (s[i] == '\\') {
				escapeNext = true;
			} else if (s[i] == '"' && !escapeNext) {
				return str.toString();
			} else {
				escapeNext = false;
				str.append(s[i]);
			}
		}
		
		return null;
	}

	/**
	 * This will consume any LWS, and check if the next character is c
	 * 
	 * @param c
	 * 
	 * @return
	 */
	public boolean consumeUntil(char c) {
		
		consumeLWS();
		
		if (i == s.length) {
			return false;
		} else if (s[i] == c) {
			i++;
			return true;
		} else {
			return false;
		}
	}
	
}
