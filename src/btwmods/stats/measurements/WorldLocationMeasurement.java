package btwmods.stats.measurements;

import net.minecraft.src.World;
import btwmods.stats.Type;

public class WorldLocationMeasurement extends WorldMeasurement {
	
	public final int x;
	public final int y;
	public final int z;
	public final int chunkX;
	public final int chunkZ;

	public WorldLocationMeasurement(Type identifier, World world, int x, int y, int z) {
		super(identifier, world);
		this.x = x;
		this.y = y;
		this.z = z;
		chunkX = x >> 4;
		chunkZ = z >> 4;
	}
}
