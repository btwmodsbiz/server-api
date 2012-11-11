package btwmods.world;

import btwmods.events.IAPIListener;

public interface IBlockListener extends IAPIListener {
	public void onBlockAction(BlockEvent event);
}
