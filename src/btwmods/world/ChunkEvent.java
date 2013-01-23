package btwmods.world;

import net.minecraft.src.Chunk;
import net.minecraft.src.World;

public class ChunkEvent extends btwmods.events.ChunkEvent {
	
	public enum TYPE { PREUNLOAD, UNLOADED };

	public static ChunkEvent Unloaded(World world, Chunk chunk) {
		return new ChunkEvent(chunk, TYPE.UNLOADED, world, chunk);
	}

	public static ChunkEvent PreUnload(World world, Chunk chunk) {
		return new ChunkEvent(chunk, TYPE.PREUNLOAD, world, chunk);
	}
	
	protected final TYPE type;
	
	public TYPE getType() {
		return type;
	}

	public ChunkEvent(Object source, TYPE type, World world, Chunk chunk) {
		super(source, world, chunk);
		this.type = type;
	}

}
