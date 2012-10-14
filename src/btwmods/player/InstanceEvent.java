package btwmods.player;

import java.util.EventObject;

import btwmods.IEventInterrupter;
import net.minecraft.src.EntityPlayerMP;

public class InstanceEvent extends EventObject implements IEventInterrupter {
	
	public enum TYPE { LOGIN, LOGOUT, RESPAWN };

	private TYPE type;
	private EntityPlayerMP playerInstance = null;
	private EntityPlayerMP playerInstanceNew = null;
	private EntityPlayerMP playerInstanceOld = null;
	private boolean respawnHandled = false;
	
	public TYPE getType() {
		return type;
	}
	
	public EntityPlayerMP getPlayerInstance() {
		return playerInstance;
	}
	
	public EntityPlayerMP getPlayerInstanceNew() {
		return playerInstanceNew;
	}
	
	public EntityPlayerMP getPlayerInstanceOld() {
		return playerInstanceOld;
	}
	
	public boolean isRespawnHandled() {
		return respawnHandled;
	}
	
	public static InstanceEvent Login(EntityPlayerMP playerInstance) {
		InstanceEvent event = new InstanceEvent(TYPE.LOGIN, playerInstance);
		event.playerInstance = playerInstance;
		return event;
	}
	
	public static InstanceEvent Logout(EntityPlayerMP playerInstance) {
		InstanceEvent event = new InstanceEvent(TYPE.LOGOUT, playerInstance);
		event.playerInstance = playerInstance;
		return event;
	}
	
	public static InstanceEvent Respawn(EntityPlayerMP playerInstanceOld, EntityPlayerMP playerInstanceNew) {
		InstanceEvent event = new InstanceEvent(TYPE.RESPAWN, playerInstanceOld);
		event.playerInstanceOld = playerInstanceOld;
		event.playerInstanceNew = playerInstanceNew;
		return event;
	}
	
	private InstanceEvent(TYPE type, EntityPlayerMP playerInstance) {
		super(playerInstance);
		this.type = type;
	}

	@Override
	public boolean isInterrupted() {
		return type == TYPE.RESPAWN && respawnHandled;
	}
}
