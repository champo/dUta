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


	private Map<Browser, Filter> browserFilter = new HashMap<Browser, Filter>();

	private Map<OperatingSystem, Filter> OSFilter = new HashMap<OperatingSystem, Filter>();

	private Map<String, Filter> IPFilter = new HashMap<String, Filter>();


	public List<Filter> getFilterList(SocketChannel socket, MessageHeader header) {

		return null;
	}


	public int addBrowserFilter(Browser broswer, Filter filter) {

		return 0;
	}

	public int addOSFilter(OperatingSystem operatingSystem, Filter filter) {

		return 0;
	}

	public int addIPFilter(String ip, Filter filter) {

		return 0;
	}


	public void removeFilter(int id) {

	}
}
