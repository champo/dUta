package ar.edu.itba.pdc.duta.proxy.filter;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;

import ar.edu.itba.pdc.duta.admin.AdminFilter;
import ar.edu.itba.pdc.duta.http.model.MessageHeader;

public class Filters {

	private int adminPort = 1337;

	private Filter adminFilter = new AdminFilter();

	private Map<Object, List<Filter>> filterMap = new HashMap<Object, List<Filter>>();


	public List<Filter> getFilterList(SocketChannel socket, MessageHeader header) {

		return null;
	}


	public int addFilter(List<Object> match, Filter filter) {

		return 0;
	}


	public void removeFilter(int id) {

	}
}
