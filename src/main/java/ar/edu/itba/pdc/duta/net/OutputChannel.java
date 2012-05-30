package ar.edu.itba.pdc.duta.net;

import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;

public interface OutputChannel {

	public void queueOutput(DataBuffer output);
	
}
