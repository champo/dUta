package ar.edu.itba.pdc.duta.admin.endpoint.stats;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.proxy.filter.http.MediaTypeFilter;

public class DenyTypeEndpoint extends AbstractStatsEndpoint {

	@Override
	protected long getValue() {
		return Stats.filterMatches(MediaTypeFilter.class);
	}

}
