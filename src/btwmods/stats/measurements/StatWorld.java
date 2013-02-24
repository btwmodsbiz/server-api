package btwmods.stats.measurements;

import net.minecraft.src.World;
import btwmods.Stat;
import btwmods.Util;

public class StatWorld extends StatTick {

	public final int worldIndex;
	
	public StatWorld(Stat identifier, int worldIndex) {
		super(identifier);
		this.worldIndex = worldIndex;
	}
	
	public StatWorld(Stat identifier, World world) {
		this(identifier, Util.getWorldIndexFromDimension(world.provider.dimensionId));
	}
}
