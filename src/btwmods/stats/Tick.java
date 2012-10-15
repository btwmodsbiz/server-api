package btwmods.stats;

import btwmods.ModLoader;
import btwmods.measure.Measurement;
import net.minecraft.src.Entity;
import net.minecraft.src.NextTickListEntry;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class Tick extends Measurement<Tick.Type> {

	public final int worldIndex;
	public final BlockTick blockTick;
	public final EntityTick entityTick;
	public final TileEntityTick tileEntityTick;
	
	public Tick(Type identifier) {
		super(identifier);
		worldIndex = -1;
		blockTick = null;
		entityTick = null;
		tileEntityTick = null;
	}
	
	public Tick(Type identifier, World world) {
		super(identifier);
		worldIndex = ModLoader.getWorldIndexFromDimension(world.provider.dimensionId);
		blockTick = null;
		entityTick = null;
		tileEntityTick = null;
	}
	
	public Tick(Type identifier, World world, NextTickListEntry entry) {
		super(identifier);
		worldIndex = ModLoader.getWorldIndexFromDimension(world.provider.dimensionId);
		blockTick = new BlockTick(entry);
		entityTick = null;
		tileEntityTick = null;
	}

	public Tick(Type identifier, World world, Entity entity) {
		super(identifier);
		worldIndex = ModLoader.getWorldIndexFromDimension(world.provider.dimensionId);
		blockTick = null;
		entityTick = new EntityTick(entity);
		tileEntityTick = null;
	}

	public Tick(Type identifier, World world, TileEntity tileEntity) {
		super(identifier);
		worldIndex = ModLoader.getWorldIndexFromDimension(world.provider.dimensionId);
		blockTick = null;
		entityTick = null;
		tileEntityTick = new TileEntityTick(tileEntity);
	}

	public enum Type {
		mobSpawning,
		blockTick,
		tickBlocksAndAmbianceSuper,
		tickBlocksAndAmbiance,
		entities,
		buildActiveChunkSet,
		checkPlayerLight,
		timeSync,
		regularentity,
		tileentity
	};
	
	public class BlockTick {
		public final int blockID;
		public final int x;
		public final int y;
		public final int z;
		public final int chunkX;
		public final int chunkZ;
		
		private BlockTick(NextTickListEntry entry) {
			blockID = entry.blockID;
			x = entry.xCoord;
			y = entry.yCoord;
			z = entry.zCoord;
			chunkX = x >> 4;
			chunkZ = z >> 4;
		}
	}
	
	public class EntityTick {
		public final int x;
		public final int y;
		public final int z;
		public final int chunkX;
		public final int chunkZ;

		private EntityTick(Entity entity) {
			x = (int)entity.posX;
			y = (int)entity.posY;
			z = (int)entity.posZ;
			chunkX = x >> 4;
			chunkZ = z >> 4;
		}
	}
	
	public class TileEntityTick {
		public final int x;
		public final int y;
		public final int z;
		public final int chunkX;
		public final int chunkZ;

		private TileEntityTick(TileEntity tileEntity) {
			x = tileEntity.xCoord;
			y = tileEntity.yCoord;
			z = tileEntity.zCoord;
			chunkX = x >> 4;
			chunkZ = z >> 4;
		}
	}
}
