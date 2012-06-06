package ar.edu.itba.pdc.duta.http.parser;

import java.io.IOException;

public interface BodyParser {

	public int parse() throws IOException;
	
	public boolean isComplete();
	
}
