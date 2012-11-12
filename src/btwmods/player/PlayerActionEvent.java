package btwmods.player;

import java.util.EventObject;
import btwmods.events.IEventInterrupter;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;

public class PlayerActionEvent extends EventObject implements IEventInterrupter {
	
	public enum TYPE { ATTACKED_BY_PLAYER, PLAYER_USE_ENTITY_ATTEMPT };
	
	private TYPE type;
	private Entity entity = null;
	private EntityPlayer player = null;
	private boolean isLeftClick = false;
	
	private boolean isHandled = false;
	private boolean isAllowed = true;
	
	public boolean isHandled() {
		return (type == TYPE.PLAYER_USE_ENTITY_ATTEMPT) && isHandled;
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
	
	public TYPE getType() {
		return type;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public EntityPlayer getPlayer() {
		return player;
	}
	
	public boolean isLeftClick() {
		return isLeftClick;
	}
	
	public static PlayerActionEvent AttackedByPlayer(Entity entity, EntityPlayer player) {
		PlayerActionEvent event = new PlayerActionEvent(player, TYPE.ATTACKED_BY_PLAYER);
		event.entity = entity;
		return event;
	}

	public static PlayerActionEvent UseEntityAttempt(EntityPlayer player, Entity entity, boolean isLeftClick) {
		PlayerActionEvent event = new PlayerActionEvent(player, TYPE.PLAYER_USE_ENTITY_ATTEMPT);
		event.entity = entity;
		event.isLeftClick = isLeftClick;
		return event;
	}
	
	private PlayerActionEvent(EntityPlayer player, TYPE type) {
		super(player);
		this.type = type;
		this.player = player;
	}

	@Override
	public boolean isInterrupted() {
		return isHandled();
	}
}
