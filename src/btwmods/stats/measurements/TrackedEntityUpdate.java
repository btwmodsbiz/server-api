package btwmods.stats.measurements;

import btwmods.stats.Type;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityTrackerEntry;
import net.minecraft.src.World;

public class TrackedEntityUpdate extends WorldLocationMeasurement {
	public final Class entity;
	public final int itemId;
	
	public TrackedEntityUpdate(World world, EntityTrackerEntry trackerEntry) {
		super(Type.UPDATE_TRACKED_ENTITY_PLAYER_LIST, world, (int)trackerEntry.trackedEntity.posX, (int)trackerEntry.trackedEntity.posY, (int)trackerEntry.trackedEntity.posZ);
		
		this.entity = trackerEntry.trackedEntity.getClass();
		itemId = trackerEntry.trackedEntity instanceof EntityItem ? ((EntityItem)trackerEntry.trackedEntity).item.itemID : -1;
	}
}
