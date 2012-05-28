package ar.edu.itba.pdc.duta.admin;

import org.apache.log4j.Logger;

public class Stats {
	
	private static final Logger logger = Logger.getLogger(Stats.class); 
	
	private static int inbound = 0;
	
	private static int outbound = 0;
	
	private static int outClosed = 0; 
	
	private static int inClosed = 0;

	private static long clientTraffic = 0;

	private static long serverTraffic = 0;
	
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
	}
	
	public static synchronized void addClientTraffic(long bytes) {
		clientTraffic += bytes;
	}
	
	public static synchronized void addServerTraffic(long bytes) {
		serverTraffic += bytes;
	}
}