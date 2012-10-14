package btwmods.server;

import btwmods.ModLoader;
import btwmods.measure.Measurement;
import net.minecraft.src.NextTickListEntry;
import net.minecraft.src.World;

public class Tick extends Measurement<Tick.Type> {

	public final int worldIndex;
	public final BlockTick blockTick;
	
	public Tick(Type identifier) {
		super(identifier);
		worldIndex = -1;
		blockTick = null;
	}
	
	public Tick(Type identifier, World world) {
		super(identifier);
		worldIndex = ModLoader.getWorldIndexFromDimension(world.provider.dimensionId);
		blockTick = null;
	}
	
	public Tick(Type identifier, World world, NextTickListEntry entry) {
		super(identifier);
		worldIndex = ModLoader.getWorldIndexFromDimension(world.provider.dimensionId);
		blockTick = new BlockTick(entry);
	}

	public enum Type {
		mobSpawning,
		blockTick,
		tickBlocksAndAmbianceSuper,
		tickBlocksAndAmbiance,
		entities,
		buildActiveChunkSet,
		checkPlayerLight,
		timeSync
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
