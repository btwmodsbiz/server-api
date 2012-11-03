package btwmods.stats.measurements;

import net.minecraft.src.World;
import btwmods.stats.Type;

public class SpawnedLiving extends WorldMeasurement {

	public final int count;
	
	public SpawnedLiving(World world, int count) {
		super(Type.SPAWN_LIVING, world);
		this.count = count;
	}

}
