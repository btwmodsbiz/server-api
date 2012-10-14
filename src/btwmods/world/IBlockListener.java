package btwmods.world;

import btwmods.IAPIListener;

public interface IBlockListener extends IAPIListener {
	public void blockAction(BlockEvent event);
}
