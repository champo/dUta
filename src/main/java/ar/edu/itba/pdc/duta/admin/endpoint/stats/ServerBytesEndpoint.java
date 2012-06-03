package ar.edu.itba.pdc.duta.admin.endpoint.stats;

import ar.edu.itba.pdc.duta.admin.Stats;

public class ServerBytesEndpoint extends AbstractStatsEndpoint {
	
	@Override
	protected long getValue() {
		return Stats.getServerTraffic();
	}

}
