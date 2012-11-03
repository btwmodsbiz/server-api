package btwmods.stats.measurements;

import btwmods.StatsAPI;
import btwmods.stats.Type;
import net.minecraft.src.Entity;
import net.minecraft.src.World;

public class EntityUpdate extends LocationMeasurement {
	public final Class entity;
	public final String name;
	
	public EntityUpdate(World world, Entity entity) {
		super(Type.ENTITY_UPDATE, world, (int)entity.posX, (int)entity.posY, (int)entity.posZ);
		this.entity = entity.getClass();
		name = StatsAPI.getEntityName(entity);
	}
}
