package btwmods.world.listeners;

import java.util.EventListener;

import btwmods.world.events.BlockEvent;

public interface IBlockListener extends EventListener {
	public void blockAction(BlockEvent event);
}
