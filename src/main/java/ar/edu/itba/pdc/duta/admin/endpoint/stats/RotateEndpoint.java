package ar.edu.itba.pdc.duta.admin.endpoint.stats;


public class RotateEndpoint extends AbstractStatsEndpoint {

	@Override
	protected long getValue() {
		return 0;
		//return Stats.filterMatches(IDontExist.class);
	}

}
