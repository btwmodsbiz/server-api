package btwmods.tick;

import net.minecraft.src.Block;
import net.minecraft.src.NextTickListEntry;
import net.minecraft.src.World;

public class BlockUpdate extends WorldMeasurement {
	
	public final int blockID;
	public final int x;
	public final int y;
	public final int z;
	public final int chunkX;
	public final int chunkZ;

	public BlockUpdate(World world, Block block, int x, int y, int z) {
		super(Type.BLOCK_UPDATE, world);
		blockID = block.blockID;
		this.x = x;
		this.y = y;
		this.z = z;
		chunkX = x >> 4;
		chunkZ = z >> 4;
	}

	public BlockUpdate(World world, NextTickListEntry entry) {
		super(Type.BLOCK_UPDATE, world);
		blockID = entry.blockID;
		x = entry.xCoord;
		y = entry.yCoord;
		z = entry.zCoord;
		chunkX = x >> 4;
		chunkZ = z >> 4;
	}
}
