package btwmods.world;

import net.minecraft.src.Chunk;
import net.minecraft.src.World;

public class ChunkEvent extends btwmods.events.ChunkEvent {
	
	public enum TYPE { UNLOADED };

	public static ChunkEvent Unloaded(World world, Chunk chunk) {
		ChunkEvent event = new ChunkEvent(chunk, TYPE.UNLOADED, world, chunk);
		return event;
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
