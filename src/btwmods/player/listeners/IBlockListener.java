package btwmods.player.listeners;

import java.util.EventListener;

import btwmods.player.events.BlockEvent;

public interface IBlockListener extends EventListener {
	public void blockActivated(BlockEvent event);
}
