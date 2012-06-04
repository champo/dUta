package ar.edu.itba.pdc.duta.proxy.operation;

import java.io.IOException;

public class EmptyParser implements BodyParser {

	@Override
	public int parse() throws IOException {
		throw new IOException("The body is empty. This method should never be called.");
	}

	@Override
	public boolean isComplete() {
		return true;
	}

}
