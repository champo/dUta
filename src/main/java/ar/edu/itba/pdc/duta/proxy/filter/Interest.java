package ar.edu.itba.pdc.duta.proxy.filter;

public class Interest {
	
	private boolean preProcess;
	
	private boolean postProcess;
	
	private boolean body;
	
	private boolean full;
	
	public Interest(boolean preProcess, boolean postProcess, boolean body, boolean full) {
		super();
		this.preProcess = preProcess;
		this.postProcess = postProcess;
		this.body = body;
		this.full = full;
	}

	public boolean preProcess() {
		return preProcess;
	}
	
	public boolean postProcess() {
		return postProcess;
	}
	
	public boolean body() {
		return body;
	}
	
	public boolean full() {
		return full;
	}

}
