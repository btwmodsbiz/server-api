package btwmods.player;

import btwmods.events.IEventInterrupter;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public class PlayerBlockEvent extends PlayerBlockEventBase implements IEventInterrupter {
	
	public static PlayerBlockEvent Activated(EntityPlayer player, Block block, int x, int y, int z) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.ACTIVATED, player);
		event.block = block;
		event.setCoordinates(x, y, z);
		return event;
	}

	public static PlayerBlockEvent ActivationAttempt(EntityPlayer player, Block block, int x, int y, int z, int direction, float xOffset, float yOffset, float zOffset) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.ACTIVATION_ATTEMPT, player);
		event.direction = direction;
		event.xOffset = xOffset;
		event.yOffset = yOffset;
		event.zOffset = zOffset;
		event.block = block;
		event.setCoordinates(x, y, z);
		return event;
	}
	
	public static PlayerBlockEvent PlaceAttempt(EntityPlayer player, ItemStack itemStack, int x, int y, int z, int direction, float xOffset, float yOffset, float zOffset) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.PLACE_ATTEMPT, player);
		event.itemStack = itemStack;
		event.direction = direction;
		event.xOffset = xOffset;
		event.yOffset = yOffset;
		event.zOffset = zOffset;
		event.setCoordinates(x, y, z);
		return event;
	}
	
	public enum TYPE { ACTIVATED, ACTIVATION_ATTEMPT, PLACE_ATTEMPT };
	
	private TYPE type;
	private boolean isHandled = false;
	private boolean isAllowed = true;

	protected ItemStack itemStack;
	protected int direction = -1;
	protected float xOffset = -1F;
	protected float yOffset = -1F;
	protected float zOffset = -1F;
	
	public TYPE getType() {
		return type;
	}
	
	public boolean isHandled() {
		return (type == TYPE.ACTIVATION_ATTEMPT || type == TYPE.PLACE_ATTEMPT) && isHandled;
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
	
	public int getDirection() {
		return direction;
	}
	
	public float getXOffset() {
		return xOffset;
	}
	
	public float getYOffset() {
		return yOffset;
	}
	
	public float getZOffset() {
		return zOffset;
	}
	
	public ItemStack getItemStack() {
		return itemStack;
	}
	
	private PlayerBlockEvent(TYPE type, EntityPlayer player) {
		super(player, player.worldObj);
		this.type = type;
	}

	@Override
	public boolean isInterrupted() {
		return isHandled();
	}
}
