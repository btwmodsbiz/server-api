package btwmods.world;

import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.World;

public class BlockEvent extends BlockEventBase {
	
	public enum TYPE { BROKEN };

	private TYPE type;
	
	public TYPE getType() {
		return type;
	}
	
	public static BlockEvent Broken(World world, Chunk chunk, Block block, int metadata, int x, int y, int z) {
		BlockEvent event = new BlockEvent(TYPE.BROKEN, world, chunk);
		event.block = block;
		event.metadata = metadata;
		event.setCoordinates(x, y, z);
		return event;
	}
	
	private BlockEvent(TYPE type, World world, Chunk chunk) {
		super(world, world);
		this.type = type;
		this.chunk = chunk;
	}
}
