package btwmods.player;

import btwmods.IAPIListener;

public interface IBlockListener extends IAPIListener {
	public void blockActivated(BlockEvent event);
}
