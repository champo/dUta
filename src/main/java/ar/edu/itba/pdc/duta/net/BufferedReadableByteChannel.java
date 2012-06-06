package ar.edu.itba.pdc.duta.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import org.apache.log4j.Logger;


public class BufferedReadableByteChannel {

	private static final Logger logger = Logger.getLogger(BufferedReadableByteChannel.class);

	private ReadableByteChannel channel;

	private final int capacity = 0x1000;

	private ByteBuffer buffer = ByteBuffer.allocate(capacity);


	public BufferedReadableByteChannel(ReadableByteChannel channel) {

		this.channel = channel;

		buffer.position(0);
		buffer.limit(0);
	}

	public int read(ByteBuffer buffer) throws IOException {

		if (this.buffer.remaining() > buffer.remaining()) {

			int bytes = buffer.remaining();
			int limit = this.buffer.limit();

			this.buffer.limit(this.buffer.position() + bytes);
			buffer.put(this.buffer);
			this.buffer.limit(limit);

			return bytes;
		}

		int bytes = this.buffer.remaining();
		buffer.put(this.buffer);

		if (buffer.remaining() >= capacity) {

			int otherBytes = channel.read(buffer);

			if (otherBytes >= 0) {
				return bytes + otherBytes;
			}

			if (bytes > 0) {
				return bytes;
			}

			return otherBytes;
		}

		this.buffer.position(0);
		this.buffer.limit(capacity);

		int otherBytes = channel.read(this.buffer);

		this.buffer.position(0);

		if (otherBytes < 0) {

			this.buffer.limit(0);

			if (bytes > 0) {
				return bytes;
			}

			return otherBytes;
		}

		if (otherBytes > buffer.remaining()) {

			int someBytes = buffer.remaining();

			this.buffer.limit(someBytes);
			buffer.put(this.buffer);
			this.buffer.limit(otherBytes);

			return bytes + someBytes;
		}

		this.buffer.limit(otherBytes);
		buffer.put(this.buffer);

		return bytes + otherBytes;
	}

	public boolean hasInput() {

		return buffer.hasRemaining();
	}
}
