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
	
	public static synchronized void log() {
		logger.info("Inbound " + inbound + " (" + inClosed + ")");
		logger.info("Outbound " + outbound + " (" + outClosed + ")");
		logger.info("Traffic with clients: " + clientTraffic);
		logger.info("Traffic with servers: " + serverTraffic);
		
		for (Map.Entry<Class<? extends Filter>, Long> entry : filterMatches.entrySet()) {
			logger.info("Filter " + entry.getKey().getName() + ": " + entry.getValue());
		}
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
}