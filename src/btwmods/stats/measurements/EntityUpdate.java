package btwmods.stats.measurements;

import btwmods.stats.Type;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.World;

public class EntityUpdate extends WorldLocationMeasurement {
	public final Class entity;
	public final int itemId;
	
	public EntityUpdate(World world, Entity entity) {
		super(Type.ENTITY_UPDATE, world, (int)entity.posX, (int)entity.posY, (int)entity.posZ);
		this.entity = entity.getClass();
		itemId = entity instanceof EntityItem ? ((EntityItem)entity).item.itemID : -1;
	}
}
