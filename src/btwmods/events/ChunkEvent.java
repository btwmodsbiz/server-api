package btwmods.events;

import net.minecraft.src.Chunk;
import net.minecraft.src.World;

public abstract class ChunkEvent extends WorldEvent {
	
	protected final Chunk chunk;
	
	public Chunk getChunk() {
		return chunk;
	}
	
	protected ChunkEvent(Object source, World world, Chunk chunk) {
		super(source, world);
		this.chunk = chunk;
	}
}
