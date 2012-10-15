package btwmods.player;

import java.util.EventObject;

import btwmods.events.IEventInterrupter;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;

public class InstanceEvent extends EventObject implements IEventInterrupter {
	
	public enum TYPE { LOGIN, LOGOUT, RESPAWN, READNBT, WRITENBT };

	private TYPE type;
	private EntityPlayer playerInstance = null;
	private RespawnPosition respawnPosition = null;
	private NBTTagCompound nbtTagCompound = null;
	
	public TYPE getType() {
		return type;
	}
	
	public EntityPlayer getPlayerInstance() {
		return playerInstance;
	}
	
	public RespawnPosition getRespawnPosition() {
		return respawnPosition;
	}
	
	public NBTTagCompound getNBTTagCompound() {
		return nbtTagCompound;
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

	public static InstanceEvent ReadFromNBT(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		InstanceEvent event = new InstanceEvent(TYPE.READNBT, player);
		event.playerInstance = player;
		event.nbtTagCompound = nbtTagCompound;
		return event;
	}

	public static InstanceEvent WriteToNBT(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		InstanceEvent event = new InstanceEvent(TYPE.WRITENBT, player);
		event.playerInstance = player;
		event.nbtTagCompound = nbtTagCompound;
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
