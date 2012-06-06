package ar.edu.itba.pdc.duta.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import net.jcip.annotations.GuardedBy;
import ar.edu.itba.pdc.duta.net.Reactor.ReactorKey;

public interface ChannelHandler extends OutputChannel {

	public void read(SocketChannel channel) throws IOException;

	public void write(SocketChannel channel) throws IOException;

	public Object keyLock();

	@GuardedBy("keyLock()")
	public void setKey(ReactorKey key, BufferedReadableByteChannel channel);

	@GuardedBy("keyLock()")
	public ReactorKey getKey();

	public void close();

	public void abort();

	public Object lock();

}
