package ar.edu.itba.pdc.duta.admin.endpoint.stats;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.proxy.filter.http.L33tFilter;

public class L33tEndpoint extends AbstractStatsEndpoint {

	@Override
	protected long getValue() {
		return Stats.filterMatches(L33tFilter.class);
	}

}
