package btwmods.stats.measurements;

import btwmods.stats.Type;
import net.minecraft.src.Block;
import net.minecraft.src.NextTickListEntry;
import net.minecraft.src.World;

public class BlockUpdate extends LocationMeasurement {
	
	public final int blockID;

	public BlockUpdate(World world, Block block, int x, int y, int z) {
		super(Type.BLOCK_UPDATE, world, x, y, z);
		blockID = block.blockID;
	}

	public BlockUpdate(World world, NextTickListEntry entry) {
		super(Type.BLOCK_UPDATE, world, entry.xCoord, entry.yCoord, entry.zCoord);
		blockID = entry.blockID;
	}
}
