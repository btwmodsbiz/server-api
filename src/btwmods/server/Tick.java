package btwmods.server;

import net.minecraft.src.NextTickListEntry;
import net.minecraft.src.World;

public class Tick extends Measurement<Tick.Type> {

	public final int dimension;
	public final BlockTick blockTick;
	
	public Tick(Type identifier, World world) {
		super(identifier);
		dimension = world.getWorldInfo().getDimension();
		blockTick = null;
	}
	
	public Tick(Type identifier, World world, NextTickListEntry entry) {
		super(identifier);
		dimension = world.getWorldInfo().getDimension();
		blockTick = new BlockTick(entry);
	}

	public enum Type {
		MobSpawning, // 
		TickUpdate, // 
		tickBlocksAndAmbianceSuper, // 
		tickBlocksAndAmbiance // 
	};
	
	private class BlockTick {
		public final int blockID;
		public final int x;
		public final int y;
		public final int z;
		
		public BlockTick(NextTickListEntry entry) {
			blockID = entry.blockID;
			x = entry.xCoord;
			y = entry.yCoord;
			z = entry.zCoord;
		}
	}
}
