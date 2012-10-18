package btwmods.tick;

import net.minecraft.src.World;
import btwmods.Util;

public class WorldMeasurement extends TickMeasurement {

	public final int worldIndex;
	
	public WorldMeasurement(Type identifier, World world) {
		super(identifier);
		worldIndex = Util.getWorldIndexFromDimension(world.provider.dimensionId);
	}
}
