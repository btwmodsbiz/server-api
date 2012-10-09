package btwmods.mods.itemlogger;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.ItemStack;

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
		if (event.getType() == BlockEvent.TYPE.BROKEN && event.getBlock() instanceof BlockContainer) {
			ItemStack[] contents = event.getContents();
			if (contents != null) {
				StringBuilder sb = new StringBuilder();
				
				for (int i = 0; i < contents.length; i++) {
					if (contents[i] != null) {
						if (sb.length() > 0) sb.append(", ");
						sb.append(contents[i].stackSize).append(" ").append(contents[i].getItemName());
					}
				}
				
				logger.log(Level.INFO, "Container broken at " + event.getX() + "/" + event.getY() + "/" + event.getZ() + (sb.length() == 0 ? " but was empty." : " and ejected: " + sb.toString()), new Object[] { "broken container", event, contents });
			}
		}
	}

}
