package btwmods.stats.measurements;

import net.minecraft.src.World;
import btwmods.stats.Type;

public class LocationMeasurement extends ChunkMeasurement {
	
	public final int x;
	public final int y;
	public final int z;

	public LocationMeasurement(Type identifier, World world, int x, int y, int z) {
		super(identifier, world, x >> 4, y >> 4);
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
