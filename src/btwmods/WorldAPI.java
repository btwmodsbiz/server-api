package btwmods;

import java.util.HashSet;

import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.World;
import btwmods.world.events.BlockEvent;
import btwmods.world.listeners.IBlockListener;

public class WorldAPI {
	//public World world;
	
	//private static ModLoader<WorldAPI,IWorldAPIMod> ModLoader = new ModLoader<WorldAPI,IWorldAPIMod>(WorldAPI.class, IWorldAPIMod.class);
	//private ModLoader.Mods mods;
	
	private static HashSet<IBlockListener> blockListeners = new HashSet<IBlockListener>();
	
	//public final static String MOD_LIST_KEY = "WorldAPI.Mods";
	
	private WorldAPI() {}

	public static void addListener(IAPIListener listener) {
		if (listener instanceof IBlockListener)
			blockListeners.add((IBlockListener)listener);
	}

	public static void removeListener(IAPIListener listener) {
		if (listener instanceof IBlockListener)
			blockListeners.remove((IBlockListener)listener);
	}
	
	public static void blockBroken(World world, Chunk chunk, Block block, int x, int y, int z, int blockID, int blockMetadata) {
		if (!blockListeners.isEmpty()) {
			BlockEvent event = BlockEvent.Broken(world, chunk, block, blockMetadata, x, y, z);
			
			for (IBlockListener listener : blockListeners)
				try {
					listener.blockAction(event);
				} catch (Throwable t) {
					ModLoader.reportListenerFailure(t, listener);
				}
		}
	}
}
