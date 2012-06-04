package ar.edu.itba.pdc.duta.proxy.filter;



public interface Filter {

	public FilterPart getRequestPart();
	
	public FilterPart getResponsePart();

	public int getPriority();
}
