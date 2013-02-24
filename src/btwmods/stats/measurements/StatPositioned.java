package btwmods.stats.measurements;

import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import btwmods.stats.Type;

public class StatPositioned extends StatChunk {
	
	public final int x;
	public final int y;
	public final int z;
	
	public final double xDouble;
	public final double yDouble;
	public final double zDouble;

	public StatPositioned(Type identifier, World world, int x, int y, int z) {
		super(identifier, world, x >> 4, y >> 4);
		this.xDouble = this.x = x;
		this.yDouble = this.y = y;
		this.zDouble = this.z = z;
	}

	public StatPositioned(Type identifier, World world, double xDouble, double yDouble, double zDouble) {
		this(identifier, world, MathHelper.floor_double(xDouble), MathHelper.floor_double(yDouble), MathHelper.floor_double(zDouble), xDouble, yDouble, zDouble);
	}
	
	private StatPositioned(Type identifier, World world, int x, int y, int z, double xDouble, double yDouble, double zDouble) {
		super(identifier, world, x >> 4, y >> 4);
		this.x = x;
		this.y = y;
		this.z = z;
		this.xDouble = xDouble;
		this.yDouble = yDouble;
		this.zDouble = zDouble;
	}
}
