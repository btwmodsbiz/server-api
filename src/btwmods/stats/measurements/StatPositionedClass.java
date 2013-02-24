package btwmods.stats.measurements;

import btwmods.StatsAPI;
import btwmods.stats.Type;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityTrackerEntry;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class StatPositionedClass extends StatPositioned {

	public final Class clazz;
	public final String name;

	public StatPositionedClass(Type type, World world, double x, double y, double z, Class clazz) {
		this(type, world, x, y, z, clazz, null);
	}

	public StatPositionedClass(Type type, World world, double x, double y, double z, Class clazz, String name) {
		super(type, world, x, y, z);
		this.clazz = clazz;
		this.name = name;
	}

	public StatPositionedClass(Type type, World world, TileEntity tileEntity) {
		this(type, world, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, tileEntity.getClass());
	}
	
	public StatPositionedClass(World world, Entity entity) {
		this(Type.ENTITY_UPDATE, world, entity.posX, entity.posY, entity.posZ, entity.getClass(), StatsAPI.getEntityName(entity));
	}
	
	public StatPositionedClass(World world, EntityTrackerEntry trackerEntry) {
		this(Type.UPDATE_TRACKED_ENTITY_PLAYER_LIST, world, trackerEntry.trackedEntity.posX, trackerEntry.trackedEntity.posY, trackerEntry.trackedEntity.posZ, trackerEntry.trackedEntity.getClass(), StatsAPI.getEntityName(trackerEntry.trackedEntity));
	}
}
