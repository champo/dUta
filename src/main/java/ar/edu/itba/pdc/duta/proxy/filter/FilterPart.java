package ar.edu.itba.pdc.duta.proxy.filter;

import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;
import ar.edu.itba.pdc.duta.proxy.operation.Operation;

public abstract class FilterPart {

	public abstract Interest checkInterest(MessageHeader header);

	public Message processHeader(Operation op, MessageHeader header) {
		return null;
	}

	public Message bytesRecieved(Operation op, Message msg, long recieved) {
		return null;
	}

	public Message filter(Operation op, Message msg) {
		return null;
	}

}
