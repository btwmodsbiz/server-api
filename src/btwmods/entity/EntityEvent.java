package btwmods.entity;

import btwmods.events.IEventInterrupter;
import btwmods.events.PositionedEvent;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;

public class EntityEvent extends PositionedEvent implements IEventInterrupter {
	
	public enum TYPE { IS_ENTITY_INVULNERABLE, TRAMPLE_FARMLAND_ATTEMPT };

	private TYPE type;
	private Entity entity;
	
	private boolean isHandled = false;
	private boolean isAllowed = true;
	private boolean invulnerable = false;
	
	private int blockX = 0;
	private int blockY = -1;
	private int blockZ = 0;
	private float distanceFallen = -1F;
	
	public TYPE getType() {
		return type;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public boolean isHandled() {
		return isHandled;
	}
	
	public void markHandled() {
		isHandled = true;
	}
	
	public boolean isAllowed() {
		return isAllowed;
	}
	
	public void markNotAllowed() {
		isAllowed = false;
	}
	
	public boolean isInvulnerable() {
		return invulnerable;
	}
	
	public void markIsInvulnerable() {
		if (type == TYPE.IS_ENTITY_INVULNERABLE)
			invulnerable = true;
	}
	
	public int getBlockX() {
		return blockX;
	}
	
	public int getBlockY() {
		return blockY;
	}
	
	public int getBlockZ() {
		return blockZ;
	}
	
	public float getDistanceFallen() {
		return distanceFallen;
	}

	public static EntityEvent CheckIsEntityInvulnerable(Entity entity) {
		return new EntityEvent(TYPE.IS_ENTITY_INVULNERABLE, entity);
	}

	public static EntityEvent TrampleFarmlandAttempt(int blockX, int blockY, int blockZ, Entity entity, float distanceFallen) {
		EntityEvent event = new EntityEvent(TYPE.TRAMPLE_FARMLAND_ATTEMPT, entity);
		event.blockX = blockX;
		event.blockY = blockY;
		event.blockZ = blockZ;
		event.distanceFallen = distanceFallen;
		return event;
	}

	private EntityEvent(TYPE type, Entity entity) {
		super(entity, entity.worldObj, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ));
		this.type = type;
		this.entity = entity;
	}

	@Override
	public boolean isInterrupted() {
		return isHandled();
	}
}
