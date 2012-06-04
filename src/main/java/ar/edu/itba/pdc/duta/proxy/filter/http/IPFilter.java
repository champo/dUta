package ar.edu.itba.pdc.duta.proxy.filter.http;

import java.net.InetAddress;
import java.util.regex.Pattern;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.http.MessageFactory;
import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.Interest;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public class IPFilter implements Filter {

	{
		Stats.registerFilterType(IPFilter.class);
	}

	private final Pattern pattern;

	public IPFilter(String ipRegex) {

		pattern = Pattern.compile(ipRegex);
	}

	@Override
	public FilterPart getRequestPart() {

		return new RequestPart();
	}

	@Override
	public FilterPart getResponsePart() {

		return null;
	}

	private class RequestPart extends FilterPart {

		@Override
		public Interest checkInterest(MessageHeader header) {

			return new Interest(true, false, false);
		}

		@Override
		public Message processHeader(Operation op, MessageHeader header) {

			InetAddress address = op.getServerProxy().getAddress().getAddress();
			if (address != null && pattern.matcher(address.getHostAddress()).matches()) {

				return MessageFactory.build404();
			}

			return null;
		}
	}

	@Override
	public int getPriority() {

		return 16;
	}
}