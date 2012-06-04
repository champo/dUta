package ar.edu.itba.pdc.duta.admin.endpoint.stats;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.proxy.filter.http.IPFilter;

public class DenyIpEndpoint extends AbstractStatsEndpoint {

	@Override
	protected long getValue() {
		return Stats.filterMatches(IPFilter.class);
	}

}
