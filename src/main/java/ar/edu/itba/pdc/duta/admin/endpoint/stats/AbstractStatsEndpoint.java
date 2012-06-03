package ar.edu.itba.pdc.duta.admin.endpoint.stats;

import ar.edu.itba.pdc.duta.admin.endpoint.Endpoint;
import ar.edu.itba.pdc.duta.http.MessageFactory;
import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;

public abstract class AbstractStatsEndpoint extends Endpoint {

	@Override
	public Message process(Message msg) {
		
		RequestHeader header = (RequestHeader) msg.getHeader();
		if (!"GET".equalsIgnoreCase(header.getMethod())) {
			return MessageFactory.build400();
		}
		
		Message res = MessageFactory.build200("{'value': " + getValue() + "}");
		res.getHeader().setField("Content-Type", "application/json; encoding=UTF-8");
		
		return res;
	}

	protected abstract long getValue();

}
