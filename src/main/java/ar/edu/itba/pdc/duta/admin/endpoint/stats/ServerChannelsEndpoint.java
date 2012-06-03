package ar.edu.itba.pdc.duta.admin.endpoint.stats;

import ar.edu.itba.pdc.duta.admin.Stats;

public class ServerChannelsEndpoint extends AbstractStatsEndpoint {

	@Override
	protected long getValue() {
		return Stats.getServerChannels();
	}

}
