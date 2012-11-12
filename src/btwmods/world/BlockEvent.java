package btwmods.world;

import btwmods.events.IEventInterrupter;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.World;

public class BlockEvent extends BlockEventBase implements IEventInterrupter {
	
	public enum TYPE { BROKEN, EXPLODE_ATTEMPT };

	private TYPE type;
	
	private boolean isHandled = false;
	private boolean isAllowed = true;
	
	public TYPE getType() {
		return type;
	}
	
	public boolean isHandled() {
		return (type == TYPE.EXPLODE_ATTEMPT) && isHandled;
	}
	
	public void markHandled() {
		isHandled = true;
	}
	
	public boolean isAllowed() {
		return isAllowed;
	}
	
	public void markNotAllowed() {
		isAllowed = false;
	}
	
	public static BlockEvent Broken(World world, Chunk chunk, Block block, int metadata, int x, int y, int z) {
		BlockEvent event = new BlockEvent(TYPE.BROKEN, world);
		event.chunk = chunk;
		event.block = block;
		event.metadata = metadata;
		event.setCoordinates(x, y, z);
		return event;
	}
	
	public static BlockEvent Exploded(World world, int blockId, int x, int y, int z) {
		BlockEvent event = new BlockEvent(TYPE.EXPLODE_ATTEMPT, world);
		event.block = blockId > 0 ? Block.blocksList[blockId] : null;
		event.setCoordinates(x, y, z);
		return event;
	}
	
	private BlockEvent(TYPE type, World world) {
		super(world, world);
		this.type = type;
	}

	@Override
	public boolean isInterrupted() {
		return isHandled();
	}
}
