package btwmods.player;

import btwmods.events.IAPIListener;

public interface IBlockListener extends IAPIListener {
	public void blockActivated(BlockEvent event);
	public void blockActivationAttempt(BlockEvent event);
}
