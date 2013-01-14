package btwmods.world;

import btwmods.events.IEventInterrupter;
import btwmods.events.PositionedEvent;
import net.minecraft.src.Block;
import net.minecraft.src.BlockDoor;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;

public class MobEvent extends PositionedEvent implements IEventInterrupter {
	
	public enum TYPE { CAN_BREAK_DOOR };

	private TYPE type;
	private Entity entity;
	
	private boolean isHandled = false;
	private boolean isAllowed = true;
	
	private Block targetBlock = null;
	private int targetX = 0;
	private int targetY = 0;
	private int targetZ = 0;
	
	public TYPE getType() {
		return type;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public Block getTargetBlock() {
		return targetBlock;
	}
	
	public int getTargetX() {
		return targetX;
	}
	
	public int getTargetY() {
		return targetY;
	}
	
	public int getTargetZ() {
		return targetZ;
	}
	
	public boolean isHandled() {
		return isHandled;
	}
	
	public void markHandled() {
		if (type == TYPE.CAN_BREAK_DOOR)
			isHandled = true;
	}
	
	public boolean isAllowed() {
		return isAllowed;
	}
	
	public void markNotAllowed() {
		if (type == TYPE.CAN_BREAK_DOOR)
			isAllowed = false;
	}

	public static MobEvent CanBreakDoor(Entity entity, BlockDoor door, int doorX, int doorY, int doorZ) {
		MobEvent event = new MobEvent(TYPE.CAN_BREAK_DOOR, entity);
		event.targetBlock = door;
		event.targetX = doorX;
		event.targetY = doorY;
		event.targetZ = doorZ;
		return event;
	}

	private MobEvent(TYPE type, Entity entity) {
		super(entity, entity.worldObj, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ));
		this.type = type;
		this.entity = entity;
	}

	@Override
	public boolean isInterrupted() {
		return isHandled();
	}
}
