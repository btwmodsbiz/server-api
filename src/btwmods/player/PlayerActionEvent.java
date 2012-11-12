package btwmods.player;

import java.util.EventObject;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;

public class PlayerActionEvent extends EventObject {
	
	public enum TYPE { ATTACKED_BY_PLAYER };
	
	private TYPE type;
	private Entity entity = null;
	private EntityPlayer player = null;
	
	public TYPE getType() {
		return type;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public EntityPlayer getPlayer() {
		return player;
	}
	
	public static PlayerActionEvent AttackedByPlayer(Entity entity, EntityPlayer player) {
		PlayerActionEvent event = new PlayerActionEvent(player, TYPE.ATTACKED_BY_PLAYER);
		event.entity = entity;
		return event;
	}
	
	private PlayerActionEvent(EntityPlayer player, TYPE type) {
		super(player);
		this.type = type;
	}
}
