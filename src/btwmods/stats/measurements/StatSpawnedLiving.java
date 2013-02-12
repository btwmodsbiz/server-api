package btwmods.stats.measurements;

import net.minecraft.src.World;
import btwmods.stats.Type;

public class StatSpawnedLiving extends StatWorld {

	public final int count;
	
	public StatSpawnedLiving(World world, int count) {
		super(Type.SPAWN_LIVING, world);
		this.count = count;
	}

}
