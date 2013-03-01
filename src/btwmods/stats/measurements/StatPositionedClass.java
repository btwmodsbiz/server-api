package btwmods.stats.measurements;

import btwmods.Stat;
import net.minecraft.src.World;

public class StatPositionedClass extends StatPositioned {

	public final Class clazz;
	public final int id;

	public StatPositionedClass(Stat stat, World world, double x, double y, double z, Class clazz) {
		this(stat, world, x, y, z, clazz, -1);
	}

	public StatPositionedClass(Stat stat, World world, double x, double y, double z, Class clazz, int id) {
		super(stat, world, x, y, z);
		this.clazz = clazz;
		this.id = id;
	}
}
