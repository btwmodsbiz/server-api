package btwmods.api.world;

import java.util.EventListener;
import java.util.HashSet;
import java.util.Properties;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import btwmods.ModLoader;
import btwmods.ModProperties;
import btwmods.api.world.events.BlockEvent;
import btwmods.api.world.listeners.IBlockListener;

public class WorldAPI {
	public World world;
	
	private static ModLoader<WorldAPI,IWorldAPIMod> ModLoader = new ModLoader<WorldAPI,IWorldAPIMod>(WorldAPI.class, IWorldAPIMod.class);
	private ModLoader.Mods mods;
	
	private HashSet<IBlockListener> blockListeners = new HashSet<IBlockListener>();
	
	public final static String MOD_LIST_KEY = "WorldAPI.Mods";
	
	public WorldAPI(World world) {
		this.world = world;
		mods = ModLoader.createMods(ModProperties.Get(MOD_LIST_KEY, ""));
		mods.initMods(this);
	}
	
	public void unload() {
		mods.unloadMods(this);
	}

	public void addListener(EventListener listener) {
		if (listener instanceof IBlockListener)
			blockListeners.add((IBlockListener)listener);
	}

	public void removeListener(EventListener listener) {
		if (listener instanceof IBlockListener)
			blockListeners.remove((IBlockListener)listener);
	}
	
	public void blockBroken(Block block, int x, int y, int z, int blockID, int blockMetadata) {
		if (!blockListeners.isEmpty()) {
			BlockEvent event = BlockEvent.Broken(this, block, blockMetadata, x, y, z);
			
			for (IBlockListener listener : blockListeners)
				listener.blockAction(event);
		}
	}
}
