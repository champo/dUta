package ar.edu.itba.pdc.duta.admin.endpoint;

import ar.edu.itba.pdc.duta.http.model.Message;

public abstract class Endpoint {

	public abstract Message process(Message msg);
	
}
