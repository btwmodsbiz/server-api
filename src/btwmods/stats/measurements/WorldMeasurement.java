package btwmods.stats.measurements;

import net.minecraft.src.World;
import btwmods.Util;
import btwmods.stats.Type;

public class WorldMeasurement extends TickMeasurement {

	public final int worldIndex;
	
	public WorldMeasurement(Type identifier, World world) {
		super(identifier);
		worldIndex = Util.getWorldIndexFromDimension(world.provider.dimensionId);
	}
}
