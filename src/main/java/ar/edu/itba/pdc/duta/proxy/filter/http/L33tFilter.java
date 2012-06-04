package ar.edu.itba.pdc.duta.proxy.filter.http;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.http.MessageFactory;
import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.net.buffer.DataBuffer;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.Interest;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class L33tFilter implements Filter {

	{
		Stats.registerFilterType(L33tFilter.class);
	}

	@Override
	public FilterPart getRequestPart() {
		return null;
	}

	@Override
	public FilterPart getResponsePart() {
		return new ResponsePart();
	}

	private static class ResponsePart extends FilterPart {

		private Logger logger = Logger.getLogger(ResponsePart.class);

		private Charset charset;

		@Override
		public Interest checkInterest(MessageHeader header) {

			boolean isText = false;

			MediaType contentType = MediaType.valueOf(header.getField("Content-Type")); 

			if (contentType.isCompatible(MediaType.TEXT_PLAIN_TYPE)) {

				String encoding = contentType.getParameters().get("charset");
				if (encoding == null) {
					encoding = "ISO-8859-1";
				}

				if (Charset.isSupported(encoding)) {
					charset = Charset.forName(encoding);
					isText = true;
				} else {
					logger.warn("Skipping l33t filtering due to unsupported encoding " + encoding);
				}
			}

			return new Interest(false, false, isText);
		}

		@Override
		public Message filter(Operation op, Message msg) {

			byte[] bytes = new byte[msg.getCurrentBodySize()];

			try {

				msg.getBody().get(bytes, 0, msg.getCurrentBodySize());

			} catch (IOException e) {

				logger.error("Failed to read message", e);
				return MessageFactory.build500();
			}

			String body = new String(bytes, charset);

			body = body.replace('e', '3').replace('a', '4').replace('i', '1').replace('o', '0');

			msg.setBody(new DataBuffer(body.getBytes(charset)));

			return null;
		}
	}

	@Override
	public int getPriority() {

		return 0;
	}

}
