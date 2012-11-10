package btwmods.server;

import btwmods.events.IAPIListener;

public interface ITickListener extends IAPIListener {
	void onTick(TickEvent event);
}
