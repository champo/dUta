package ar.edu.itba.pdc.duta.admin;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.proxy.filter.Filter;

public class Stats {
	
	private static final Logger logger = Logger.getLogger(Stats.class); 
	
	private static int inbound = 0;
	
	private static int outbound = 0;
	
	private static int outClosed = 0; 
	
	private static int inClosed = 0;

	private static long clientTraffic = 0;

	private static long serverTraffic = 0;
	
	private static Map<Class<? extends Filter>, Long> filterMatches= new HashMap<Class<? extends Filter>, Long>();
	
	public static synchronized long getClientTraffic() {
		return clientTraffic;
	}
	
	public static synchronized long getServerTraffic() {
		return serverTraffic;
	}
	
	public static synchronized void newInbound() {
		inbound++;
	}
	
	public static synchronized void newOutbound() {
		outbound++;
	}
	
	public static synchronized void closeInbound() {
		inClosed++;
	}
	
	public static synchronized void closeOutbound() {
		outClosed++;
	}
	
	public static synchronized void addClientTraffic(long bytes) {
		clientTraffic += bytes;
	}
	
	public static synchronized void addServerTraffic(long bytes) {
		serverTraffic += bytes;
	}
	
	public static synchronized void registerFilterType(Class<? extends Filter> filter) {
		
		if (!filterMatches.containsKey(filter)) {
			filterMatches.put(filter, 0L);
		}
	}
	
	public static synchronized void applyFilter(Class<? extends Filter> filter) {
		filterMatches.put(filter, filterMatches.get(filter) + 1L);
	}

	public static synchronized long getServerChannels() {
		return outbound - outClosed;
	}
	
	public static synchronized long getClientChannels() {
		return inbound - inClosed;
	}

	public static synchronized long filterMatches(Class<? extends Filter> clz) {
		Long res = filterMatches.get(clz);
		if (res == null) {
			return 0;
		}
		
		return res;
	}
}