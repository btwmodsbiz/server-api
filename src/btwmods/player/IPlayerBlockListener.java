package btwmods.player;

import btwmods.events.IAPIListener;

public interface IBlockListener extends IAPIListener {
	public void blockAction(BlockEvent event);
}
