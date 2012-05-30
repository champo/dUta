package ar.edu.itba.pdc.duta.http.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.duta.http.Grammar;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;

public abstract class MessageParser {

	private enum States {
		START_LINE, BEGINNING_OF_LINE, FIELD_NAME, FIELD_VALUE, END_OF_HEADER 
	}

	private DataBuffer buffer;
	private States state;
	private int line;
	private StringBuilder currString;
	private String fieldName;
	protected Map<String, StringBuilder> fields;
	
	public MessageParser(DataBuffer buffer) {

		this.buffer = buffer;
		this.state = States.START_LINE;
		this.line = 0;
		this.currString = new StringBuilder();
		this.fieldName = null;
		this.fields = new HashMap<String, StringBuilder>();
	}

	public boolean parse() throws ParseException, IOException {
		
		char oldc;
		char c = '\0';

		while (state != States.END_OF_HEADER && buffer.hasReadableBytes()) {

			oldc = c;
			c = (char)buffer.get();
			
			switch (state) {
				
				case START_LINE:
					
					if (c == '\n') {
					
						String s = currString.toString().trim();
						currString.setLength(0);
						
						if (s.length() > 0) {
							
							try {
								setStartLine(s);
							} catch (ParseException e) {
								throw new ParseException(e, line);
							}
							state = States.BEGINNING_OF_LINE;
						}
						line++;
						break;
					}
					currString.append(c);
					break;
	
				case BEGINNING_OF_LINE:
					
					switch(c) {
					
						case '\r':
							break;
							
						case '\n':
							line++;
							state = States.END_OF_HEADER;
							break;
							
						case ' ':
						case '\t':
							state = States.FIELD_VALUE;
							break;
							
						default:
							state = States.FIELD_NAME;
							break;
					}
					break;
				
				case FIELD_NAME:

					if (!Grammar.isTokenCharacter(oldc)) {
						throw new ParseException("Invalid field name", line);
					}

					currString.append(oldc);

					if (c == ':') {

						fieldName = currString.toString();
						currString.setLength(0);
						state = States.FIELD_VALUE;

						if (fieldName.length() == 0) {
							throw new ParseException("Missing field name", line);
						}
						
						if (fields.containsKey(fieldName)) {
							fields.get(fieldName).append(", ");
						} else {
							fields.put(fieldName, new StringBuilder());
						}
						break;
					}
					break;
					
				case FIELD_VALUE:
					
					if (c == '\r') {
						c = ' ';
					}
					
					if (c == '\n') {
						
						if (fieldName == null) {
							throw new ParseException("Missing field name", line);
						}

						line++;
						fields.get(fieldName).append(' ').append(currString);
						currString.setLength(0);
						state = States.BEGINNING_OF_LINE;
						break;
					}
					
					if (Grammar.isControlCharacter(c)) {
						throw new ParseException("Invalid field value", line);
					}
					
					currString.append(c);
					break;
			}
		}

		return state == States.END_OF_HEADER;
	}
	
	protected abstract void setStartLine(String s) throws ParseException;

	protected abstract MessageHeader createHeader(Map<String, String> fields);
	
	public MessageHeader getHeader() {
		
		if (state != States.END_OF_HEADER) {
			return null;
		}

		Map<String, String> fields = new HashMap<String, String>();

		for (Map.Entry<String, StringBuilder> field : this.fields.entrySet()) {
			fields.put(field.getKey(), field.getValue().toString().trim());
		}

		return createHeader(fields);
	}
}

