package btwmods.stats.measurements;

import net.minecraft.src.World;
import btwmods.Stat;
import btwmods.Util;

public class StatWorld extends StatTick {

	public final int worldIndex;
	
	public StatWorld(Stat identifier, World world) {
		super(identifier);
		worldIndex = Util.getWorldIndexFromDimension(world.provider.dimensionId);
	}
}
