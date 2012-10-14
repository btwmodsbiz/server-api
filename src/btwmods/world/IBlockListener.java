package btwmods.world.listeners;

import btwmods.IAPIListener;
import btwmods.world.events.BlockEvent;

public interface IBlockListener extends IAPIListener {
	public void blockAction(BlockEvent event);
}
