package ar.edu.itba.pdc.duta.net;

import java.nio.ByteBuffer;

public interface OutputChannel {

	public void queueOutput(ByteBuffer output);
	
}
