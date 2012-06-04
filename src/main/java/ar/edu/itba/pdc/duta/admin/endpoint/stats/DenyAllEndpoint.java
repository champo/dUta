package ar.edu.itba.pdc.duta.admin.endpoint.stats;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.proxy.filter.http.BlockFilter;

public class DenyAllEndpoint extends AbstractStatsEndpoint {

	@Override
	protected long getValue() {
		return Stats.filterMatches(BlockFilter.class);
	}

}
