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

	public StatPositionedClass(Type type, World world, int x, int y, int z, Class clazz) {
		this(type, world, x, y, z, clazz, null);
	}

	public StatPositionedClass(Type type, World world, int x, int y, int z, Class clazz, String name) {
		super(type, world, x, y, z);
		this.clazz = clazz;
		this.name = name;
	}

	public StatPositionedClass(Type type, World world, TileEntity tileEntity) {
		this(type, world, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, tileEntity.getClass());
	}
	
	public StatPositionedClass(World world, Entity entity) {
		this(Type.ENTITY_UPDATE, world, (int)entity.posX, (int)entity.posY, (int)entity.posZ, entity.getClass(), StatsAPI.getEntityName(entity));
	}
	
	public StatPositionedClass(World world, EntityTrackerEntry trackerEntry) {
		this(Type.UPDATE_TRACKED_ENTITY_PLAYER_LIST, world, (int)trackerEntry.trackedEntity.posX, (int)trackerEntry.trackedEntity.posY, (int)trackerEntry.trackedEntity.posZ, trackerEntry.trackedEntity.getClass(), StatsAPI.getEntityName(trackerEntry.trackedEntity));
	}
}
