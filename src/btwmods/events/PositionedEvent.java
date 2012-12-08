package btwmods.events;

import net.minecraft.src.Chunk;
import net.minecraft.src.World;

public abstract class PositionedEvent extends WorldEvent {
	
	protected Chunk chunk = null;
	
	protected final int x;
	protected final int y;
	protected final int z;
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public Chunk getChunk() {
		if (chunk == null) {
			chunk = world.getChunkFromBlockCoords(x, z);
		}
		
		return chunk;
	}
	
	protected PositionedEvent(Object source, World world, int x, int y, int z) {
		super(source, world);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	protected PositionedEvent(Object source, World world, Chunk chunk, int x, int y, int z) {
		this(source, world, x, y, z);
		this.chunk = chunk;
	}

}
