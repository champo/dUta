package ar.edu.itba.pdc.duta.proxy.operation;

import java.io.IOException;

import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;

public class ChunkedParser implements BodyParser {
	
	private enum State {
		SIZE_LINE,
		CHUNK,
		CRLF,
		END,
		DONE
	}
	
	private State state = State.SIZE_LINE;

	private int nextChunkSize;

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
		byte b;
		DataBuffer body = msg.getBody();
		
		while (state != State.DONE) {
			switch (state) {
			case SIZE_LINE:
				
				if (sizeLine == null) {
					sizeLine = new StringBuilder();
				}
				
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
					String line = sizeLine.toString();
					nextChunkSize = Integer.valueOf(line.substring(0, line.length() - 2).trim(), 16);
				} catch (NumberFormatException e) {
					throw new IOException("Recieved invalid chunk size", e);
				}
				
				if (needsBody) {
					body.setWriteIndex(body.getWriteIndex() - sizeLine.length());
				}
				
				sizeLine = null;
				state = nextChunkSize == 0 ? State.END : State.CHUNK;
				break;
			case CRLF:
			case END:
				
				do {
					int pos = body.getWriteIndex();
					b = body.get();

					if (pos >= body.getWriteIndex()) {
						return read;
					}
					
					if (needsBody) {
						body.setWriteIndex(pos);
					}
					
					read++;
				} while (b != 10);
				
				state = state == State.END ? State.DONE : State.SIZE_LINE;
				
				break;
			case CHUNK:
				
				int pos = body.getWriteIndex();
				body.consume(nextChunkSize);
				int consumed = body.getWriteIndex() - pos;
				
				read += consumed;
				nextChunkSize -= consumed;
				
				if (nextChunkSize == 0) {
					state = State.CRLF;
				}
				
				break;
			default:
				break;
			}
		}
		
		return read;
	}

	@Override
	public boolean isComplete() {
		return state == State.DONE;
	}

}
