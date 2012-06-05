package ar.edu.itba.pdc.duta.proxy.operation;

import ar.edu.itba.pdc.duta.net.OutputChannel;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;
import ar.edu.itba.pdc.duta.proxy.AbstractChannelHandler;

public class ClientProxy implements OutputChannel {

	private Operation op;
	
	private AbstractChannelHandler channel;
	
	public ClientProxy(Operation op, AbstractChannelHandler channel) {
		super();
		this.op = op;
		this.channel = channel;
	}

	@Override
	public void queueOutput(DataBuffer output) {
		channel.queueOutput(output, op);
	}

}
