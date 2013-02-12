package btwmods.stats.measurements;

import net.minecraft.src.World;
import btwmods.stats.Type;

public class StatChunk extends StatWorld {
	
	public final int chunkX;
	public final int chunkZ;

	public StatChunk(Type identifier, World world, int chunkX, int chunkY) {
		super(identifier, world);
		this.chunkX = chunkX;
		this.chunkZ = chunkY;
	}
}
