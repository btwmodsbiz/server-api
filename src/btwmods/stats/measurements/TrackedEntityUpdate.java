package btwmods.stats.measurements;

import btwmods.StatsAPI;
import btwmods.stats.Type;
import net.minecraft.src.EntityTrackerEntry;
import net.minecraft.src.World;

public class TrackedEntityUpdate extends LocationMeasurement {
	public final Class entity;
	public final String name;
	
	public TrackedEntityUpdate(World world, EntityTrackerEntry trackerEntry) {
		super(Type.UPDATE_TRACKED_ENTITY_PLAYER_LIST, world, (int)trackerEntry.trackedEntity.posX, (int)trackerEntry.trackedEntity.posY, (int)trackerEntry.trackedEntity.posZ);
		this.entity = trackerEntry.trackedEntity.getClass();
		name = StatsAPI.getEntityName(trackerEntry.trackedEntity);
	}
}
