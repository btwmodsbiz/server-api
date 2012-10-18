package btwmods.stats.measurements;

import btwmods.stats.Type;
import net.minecraft.src.Entity;
import net.minecraft.src.World;

public class EntityUpdate extends WorldMeasurement {
	public final Class entity;
	public final int x;
	public final int y;
	public final int z;
	public final int chunkX;
	public final int chunkZ;
	
	public EntityUpdate(World world, Entity entity) {
		super(Type.ENTITY_UPDATE, world);
		this.entity = entity.getClass();
		x = (int)entity.posX;
		y = (int)entity.posY;
		z = (int)entity.posZ;
		chunkX = x >> 4;
		chunkZ = z >> 4;
	}
}
