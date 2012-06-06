package ar.edu.itba.pdc.duta.admin.endpoint.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ar.edu.itba.pdc.duta.admin.endpoint.Endpoint;
import ar.edu.itba.pdc.duta.http.MessageFactory;
import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.net.Server;

public class DeleteFilterEndpoint extends Endpoint {

	private final Pattern urlPattern = Pattern.compile("^/filters/([0-9]+)$");

	@Override
	public Message process(Message msg) {
		
		RequestHeader header = (RequestHeader) msg.getHeader();
		
		String uri = header.getRequestURI();
		Matcher matcher = urlPattern.matcher(uri);
		if (!matcher.matches()) {
			return MessageFactory.build404();
		}
		
		String idString = matcher.group(1);
		int id;
		try {
			id = Integer.parseInt(idString);
		} catch (NumberFormatException e) {
			// This should happen after the regexp, so lets just 400 out
			return MessageFactory.build400();
		}
		
		if (!"DELETE".equalsIgnoreCase(header.getMethod())) {
			return MessageFactory.build400();
		}
		
		if (Server.getFilters().removeFilter(id)) {
			return MessageFactory.build(204, "No Content", null);
		} else {
			return MessageFactory.build404();
		}
	}

}
