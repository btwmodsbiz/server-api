package btwmods.api.player.listeners;

import java.util.EventListener;
import btwmods.api.player.events.BlockEvent;

public interface IBlockListener extends EventListener {
	public void blockActivated(BlockEvent event);
}
