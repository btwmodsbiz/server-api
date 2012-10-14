package btwmods.player.listeners;

import btwmods.IAPIListener;
import btwmods.player.events.BlockEvent;

public interface IBlockListener extends IAPIListener {
	public void blockActivated(BlockEvent event);
}
