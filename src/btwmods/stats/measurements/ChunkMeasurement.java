package btwmods.stats.measurements;

import net.minecraft.src.World;
import btwmods.stats.Type;

public class ChunkMeasurement extends WorldMeasurement {
	
	public final int chunkX;
	public final int chunkZ;

	public ChunkMeasurement(Type identifier, World world, int chunkX, int chunkY) {
		super(identifier, world);
		this.chunkX = chunkX;
		this.chunkZ = chunkY;
	}
}
