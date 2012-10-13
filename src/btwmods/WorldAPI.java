package btwmods;

import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.World;
import btwmods.world.events.BlockEvent;
import btwmods.world.listeners.IBlockListener;

public class WorldAPI {
	private static EventDispatcher listeners = EventDispatcherFactory.create(new Class[] { IBlockListener.class });
	
	private WorldAPI() {}

	public static void addListener(IAPIListener listener) {
		listeners.addListener(listener);
	}

	public static void removeListener(IAPIListener listener) {
		listeners.addListener(listener);
	}
	
	public static void blockBroken(World world, Chunk chunk, Block block, int x, int y, int z, int blockID, int blockMetadata) {
		BlockEvent event = BlockEvent.Broken(world, chunk, block, blockMetadata, x, y, z);
		((IBlockListener)listeners).blockAction(event);
	}
}
