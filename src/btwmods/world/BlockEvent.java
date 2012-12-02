package btwmods.world;

import btwmods.events.IEventInterrupter;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.World;

public class BlockEvent extends BlockEventBase implements IEventInterrupter {
	
	public enum TYPE { BROKEN, EXPLODE_ATTEMPT, BURN_ATTEMPT, FIRE_SPREAD_ATTEMPT, IS_FLAMMABLE_BLOCK };

	private TYPE type;
	
	private boolean isHandled = false;
	private boolean isAllowed = true;
	private boolean isFlammable = true;
	
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
	
	public boolean isFlammable() {
		return isFlammable;
	}
	
	public void markNotFlammable() {
		isFlammable = false;
	}
	
	public static BlockEvent Broken(World world, Chunk chunk, Block block, int metadata, int x, int y, int z) {
		BlockEvent event = new BlockEvent(TYPE.BROKEN, world, x, y, z);
		event.chunk = chunk;
		event.block = block;
		event.metadata = metadata;
		return event;
	}
	
	public static BlockEvent ExplodeAttempt(World world, int blockId, int x, int y, int z) {
		BlockEvent event = new BlockEvent(TYPE.EXPLODE_ATTEMPT, world, x, y, z);
		event.block = blockId > 0 ? Block.blocksList[blockId] : null;
		return event;
	}

	public static BlockEvent BurnAttempt(World world, int blockId, int x, int y, int z) {
		BlockEvent event = new BlockEvent(TYPE.BURN_ATTEMPT, world, x, y, z);
		event.block = blockId > 0 ? Block.blocksList[blockId] : null;
		return event;
	}

	public static BlockEvent FireSpreadAttempt(World world, int x, int y, int z) {
		BlockEvent event = new BlockEvent(TYPE.FIRE_SPREAD_ATTEMPT, world, x, y, z);
		return event;
	}

	public static BlockEvent IsFlammableBlock(World world, int x, int y, int z) {
		BlockEvent event = new BlockEvent(TYPE.IS_FLAMMABLE_BLOCK, world, x, y, z);
		return event;
	}
	
	private BlockEvent(TYPE type, World world, int x, int y, int z) {
		super(world, world, x, y, z);
		this.type = type;
	}

	@Override
	public boolean isInterrupted() {
		return isHandled();
	}
}
