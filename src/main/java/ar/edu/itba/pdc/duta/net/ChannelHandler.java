package ar.edu.itba.pdc.duta.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import ar.edu.itba.pdc.duta.net.Reactor.ReactorKey;

public interface ChannelHandler extends OutputChannel {

	public void read(SocketChannel channel) throws IOException;

	public void write(SocketChannel channel) throws IOException;
	
	public void setKey(ReactorKey key);

	public ReactorKey getKey();

	public SocketAddress getAddress();
	
}