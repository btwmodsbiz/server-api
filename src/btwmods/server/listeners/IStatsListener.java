package btwmods.server.listeners;

import java.util.EventListener;
import btwmods.server.events.StatsEvent;

public interface IStatsListener extends EventListener {
	public void statsAction(StatsEvent event);
}
