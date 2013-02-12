package btwmods.stats.measurements;

import net.minecraft.src.World;
import btwmods.Util;
import btwmods.stats.Type;

public class StatWorld extends StatTick {

	public final int worldIndex;
	
	public StatWorld(Type identifier, World world) {
		super(identifier);
		worldIndex = Util.getWorldIndexFromDimension(world.provider.dimensionId);
	}
}
