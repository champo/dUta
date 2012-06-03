package ar.edu.itba.pdc.duta.proxy.operation;

import java.io.IOException;

public interface BodyParser {

	public int parse() throws IOException;
	
	public boolean isComplete();
	
}
