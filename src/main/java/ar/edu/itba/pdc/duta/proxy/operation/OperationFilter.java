package ar.edu.itba.pdc.duta.proxy.operation;

import ar.edu.itba.pdc.duta.proxy.filter.FilterPart;
import ar.edu.itba.pdc.duta.proxy.filter.Interest;

class OperationFilter {
	
	FilterPart part;
	
	Interest interest;
	
	public OperationFilter(FilterPart part, Interest interest) {
		super();
		this.part = part;
		this.interest = interest;
	}

	public FilterPart getPart() {
		return part;
	}
	
	public Interest getInterest() {
		return interest;
	}
}