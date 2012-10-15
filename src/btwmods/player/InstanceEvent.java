package btwmods.player;

import java.util.EventObject;

import btwmods.IEventInterrupter;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;

public class InstanceEvent extends EventObject implements IEventInterrupter {
	
	public enum TYPE { LOGIN, LOGOUT, RESPAWN };

	private TYPE type;
	private EntityPlayer playerInstance = null;
	private RespawnPosition respawnPosition = null;
	
	public TYPE getType() {
		return type;
	}
	
	public EntityPlayer getPlayerInstance() {
		return playerInstance;
	}
	
	public RespawnPosition getRespawnPosition() {
		return respawnPosition;
	}
	
	public void setRespawnPosition(RespawnPosition respawnPosition) {
		this.respawnPosition = respawnPosition;
	}
	
	public static InstanceEvent Login(EntityPlayer playerInstance) {
		InstanceEvent event = new InstanceEvent(TYPE.LOGIN, playerInstance);
		event.playerInstance = playerInstance;
		return event;
	}
	
	public static InstanceEvent Logout(EntityPlayer playerInstance) {
		InstanceEvent event = new InstanceEvent(TYPE.LOGOUT, playerInstance);
		event.playerInstance = playerInstance;
		return event;
	}
	
	public static InstanceEvent Respawn(EntityPlayer playerInstance) {
		InstanceEvent event = new InstanceEvent(TYPE.RESPAWN, playerInstance);
		event.playerInstance = playerInstance;
		return event;
	}
	
	private InstanceEvent(TYPE type, EntityPlayer playerInstance) {
		super(playerInstance);
		this.type = type;
	}

	@Override
	public boolean isInterrupted() {
		return type == TYPE.RESPAWN && respawnPosition != null;
	}
}
