package btwmods.server.listeners;

import btwmods.IAPIListener;
import btwmods.server.events.StatsEvent;

public interface IStatsListener extends IAPIListener {
	public void statsAction(StatsEvent event);
}
