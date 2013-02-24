package btwmods.stats.measurements;

import net.minecraft.src.World;
import btwmods.Stat;
import btwmods.Util;

public class StatWorldValue extends StatValue {

	public final int worldIndex;
	
	public StatWorldValue(Stat identifier, int worldIndex, long value) {
		super(identifier, value);
		this.worldIndex = worldIndex;
	}
	
	public StatWorldValue(Stat identifier, World world, long value) {
		this(identifier, Util.getWorldIndexFromDimension(world.provider.dimensionId), value);
	}

}
