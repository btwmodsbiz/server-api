package btwmods.world;

import java.util.Collections;
import java.util.List;

import btwmods.events.WorldEvent;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EnumCreatureType;
import net.minecraft.src.World;

public class SpawnLivingEvent extends WorldEvent {
	
	public final EnumCreatureType creatureType;
	public final int validChunks;
	public final int oldEntityCount;
	public final List<EntityLiving> entities;
	
	public SpawnLivingEvent(World world, EnumCreatureType creatureType, int validChunks, int oldEntityCount, List<EntityLiving> entities) {
		super(world, world);
		this.creatureType = creatureType;
		this.validChunks = validChunks;
		this.oldEntityCount = oldEntityCount;
		this.entities = Collections.unmodifiableList(entities);
	}
}
