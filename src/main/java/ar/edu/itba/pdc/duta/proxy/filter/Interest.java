package ar.edu.itba.pdc.duta.proxy.filter;

public class Interest {
	
	private boolean preProcess;
	
	private boolean append;
	
	private boolean full;
	
	public Interest(boolean preProcess, boolean append, boolean full) {
		super();
		this.preProcess = preProcess;
		this.append = append;
		this.full = full;
	}

	public boolean preProcess() {
		return preProcess;
	}
	
	public boolean append() {
		return append;
	}
	
	public boolean full() {
		return full;
	}

}
