package btwmods.mods.itemlogger;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.src.BlockContainer;

import btwmods.api.player.PlayerAPI;
import btwmods.api.world.IWorldAPIMod;
import btwmods.api.world.WorldAPI;
import btwmods.api.world.events.BlockEvent;
import btwmods.api.world.listeners.IBlockListener;

public class WorldListener implements IWorldAPIMod, IBlockListener {

	private WorldAPI api;
	private Logger logger;
	
	@Override
	public void init(WorldAPI parent) {
		api = parent;
		logger = ItemLogger.GetLogger();
		parent.addListener(this);
	}

	@Override
	public void unload(WorldAPI parent) {
		parent.removeListener(this);
	}

	@Override
	public void blockAction(BlockEvent event) {
		if (event.getType() == BlockEvent.TYPE.REMOVED_CONTAINER) {
			BlockContainer block = (BlockContainer)event.getBlock();
			logger.log(Level.INFO, "Container destroyed at x" + event.getX() + " y" + event.getY() + " z" + event.getZ() + " and ejected: ",
					new Object[] { "deposited", event, deposited, depositedQuantity });
		}
	}

}
