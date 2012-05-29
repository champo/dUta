package ar.edu.itba.pdc.duta.proxy.filter;

public class Interest {
	
	private boolean preProcess;
	
	private boolean bytesRecieved;
	
	private boolean full;
	
	public Interest(boolean preProcess, boolean bytesRecieved, boolean full) {
		super();
		this.preProcess = preProcess;
		this.bytesRecieved = bytesRecieved;
		this.full = full;
	}

	public boolean preProcess() {
		return preProcess;
	}
	
	public boolean bytesRecieved() {
		return bytesRecieved;
	}
	
	public boolean full() {
		return full;
	}

}
