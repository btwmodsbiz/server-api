package btwmods.stats.measurements;

import net.minecraft.src.World;
import btwmods.Stat;

public class StatSpawnedLiving extends StatWorld {

	public final int count;
	
	public StatSpawnedLiving(World world, int count) {
		super(Stat.SPAWN_LIVING, world);
		this.count = count;
	}

}
