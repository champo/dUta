package ar.edu.itba.pdc.duta.proxy.operation;

import java.io.IOException;

import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;

public class ChunkedParser implements BodyParser {

	private int nextChunkSize = -1;

	private Message msg;

	private boolean needsBody;
	
	private StringBuilder sizeLine;

	public ChunkedParser(Message msg, boolean needsBody) {
		this.msg = msg;
		this.needsBody = needsBody;
	}

	@Override
	public int parse() throws IOException {
		
		int read = 0;
		DataBuffer body = msg.getBody();
		if (nextChunkSize == -1) {
			
			if (sizeLine == null) {
				sizeLine = new StringBuilder();
			}

			byte b;
			do {
			
				int pos = body.getWriteIndex();
				b = body.get();

				if (pos >= body.getWriteIndex()) {
					return read;
				}
				
				read++;

				sizeLine.append((char) b);
				
			} while (b != 10);
			
			try {
				nextChunkSize = Integer.valueOf(sizeLine.toString());
			} catch (NumberFormatException e) {
				throw new IOException("Who gives a crap.");
			}
			
			if (needsBody) {
				body.setWriteIndex(body.getWriteIndex() - sizeLine.length());
			}
			
			if (nextChunkSize == 0) {
				return read;
			}
		}
		
		int pos = body.getWriteIndex();
		body.consume(nextChunkSize);
		int consumed = body.getWriteIndex() - pos;
		
		read += consumed;
		nextChunkSize -= consumed;
		
		return read;
	}

	@Override
	public boolean isComplete() {
		return nextChunkSize == 0;
	}

}
