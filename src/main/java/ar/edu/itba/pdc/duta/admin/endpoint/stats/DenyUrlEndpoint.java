package ar.edu.itba.pdc.duta.admin.endpoint.stats;

import ar.edu.itba.pdc.duta.admin.Stats;
import ar.edu.itba.pdc.duta.proxy.filter.http.URIFilter;

public class DenyUrlEndpoint extends AbstractStatsEndpoint {

	@Override
	protected long getValue() {
		return Stats.filterMatches(URIFilter.class);
	}

}
