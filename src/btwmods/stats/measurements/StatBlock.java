package btwmods.stats.measurements;

import btwmods.stats.Type;
import net.minecraft.src.Block;
import net.minecraft.src.NextTickListEntry;
import net.minecraft.src.World;

public class StatBlock extends StatPositioned {
	
	public final int blockID;

	public StatBlock(Type identifier, World world, Block block, int x, int y, int z) {
		super(identifier, world, x, y, z);
		blockID = block.blockID;
	}

	public StatBlock(Type identifier, World world, NextTickListEntry entry) {
		super(identifier, world, entry.xCoord, entry.yCoord, entry.zCoord);
		blockID = entry.blockID;
	}
}
