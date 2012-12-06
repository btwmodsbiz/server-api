package btwmods.world;

import btwmods.events.IAPIListener;

public interface IWorldTickListener extends IAPIListener {
	void onWorldTick(WorldTickEvent event);
}
