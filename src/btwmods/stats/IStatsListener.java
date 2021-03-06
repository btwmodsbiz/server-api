package btwmods.stats;

import btwmods.events.IAPIListener;

public interface IStatsListener extends IAPIListener {
	
	/**
	 * WARNING: This will execute in a separate thread. Do not interact with other classes as much as possible unless
	 * they are only used in statsAction() calls.
	 * 
	 * @param event The event data.
	 */
	public void onStats(StatsEvent event);
}
