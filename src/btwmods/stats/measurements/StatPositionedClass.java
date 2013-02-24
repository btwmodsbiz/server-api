package btwmods.stats.measurements;

import btwmods.StatsAPI;
import btwmods.Stat;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityTrackerEntry;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class StatPositionedClass extends StatPositioned {

	public final Class clazz;
	public final String name;

	public StatPositionedClass(Stat stat, World world, double x, double y, double z, Class clazz) {
		this(stat, world, x, y, z, clazz, null);
	}

	public StatPositionedClass(Stat stat, World world, double x, double y, double z, Class clazz, String name) {
		super(stat, world, x, y, z);
		this.clazz = clazz;
		this.name = name;
	}

	public StatPositionedClass(Stat stat, World world, TileEntity tileEntity) {
		this(stat, world, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, tileEntity.getClass());
	}
	
	public StatPositionedClass(World world, Entity entity) {
		this(Stat.ENTITY_UPDATE, world, entity.posX, entity.posY, entity.posZ, entity.getClass(), StatsAPI.getEntityName(entity));
	}
	
	public StatPositionedClass(World world, EntityTrackerEntry trackerEntry) {
		this(Stat.UPDATE_TRACKED_ENTITY_PLAYER_LIST, world, trackerEntry.trackedEntity.posX, trackerEntry.trackedEntity.posY, trackerEntry.trackedEntity.posZ, trackerEntry.trackedEntity.getClass(), StatsAPI.getEntityName(trackerEntry.trackedEntity));
	}
}
