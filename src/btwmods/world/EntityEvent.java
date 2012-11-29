package btwmods.world;

import btwmods.events.IEventInterrupter;
import btwmods.events.PositionedEvent;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;

public class EntityEvent extends PositionedEvent implements IEventInterrupter {
	
	public enum TYPE { EXPLODE_ATTEMPT, IS_ENTITY_INVULNERABLE };

	private TYPE type;
	private Entity entity;
	
	private boolean isHandled = false;
	private boolean isAllowed = true;
	private boolean invulnerable = false;
	
	public TYPE getType() {
		return type;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public boolean isHandled() {
		return (type == TYPE.EXPLODE_ATTEMPT) && isHandled;
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

	public static EntityEvent ExplodeAttempt(Entity entity) {
		EntityEvent event = new EntityEvent(TYPE.EXPLODE_ATTEMPT, entity);
		return event;
	}

	public static EntityEvent CheckIsEntityInvulnerable(Entity entity) {
		EntityEvent event = new EntityEvent(TYPE.IS_ENTITY_INVULNERABLE, entity);
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
