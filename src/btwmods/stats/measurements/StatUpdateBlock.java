package btwmods.stats.measurements;

import btwmods.stats.Type;
import net.minecraft.src.Block;
import net.minecraft.src.NextTickListEntry;
import net.minecraft.src.World;

public class StatUpdateBlock extends StatPositioned {
	
	public final int blockID;

	public StatUpdateBlock(World world, Block block, int x, int y, int z) {
		super(Type.BLOCK_UPDATE, world, x, y, z);
		blockID = block.blockID;
	}

	public StatUpdateBlock(World world, NextTickListEntry entry) {
		super(Type.BLOCK_UPDATE, world, entry.xCoord, entry.yCoord, entry.zCoord);
		blockID = entry.blockID;
	}
}
