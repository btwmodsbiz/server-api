package btwmods.events;

import net.minecraft.src.Chunk;
import net.minecraft.src.World;

public abstract class PositionedEvent extends APIEvent {
	
	protected final World world;
	protected Chunk chunk = null;
	
	protected final int x;
	protected final int y;
	protected final int z;
	
	public World getWorld() {
		return world;
	}
	
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
		super(source);
		
		if (world == null)
			throw new NullPointerException("world cannot be null.");
		
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

}
