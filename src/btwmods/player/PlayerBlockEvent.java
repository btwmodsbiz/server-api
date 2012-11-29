package btwmods.player;

import btwmods.events.IEventInterrupter;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public class PlayerBlockEvent extends PlayerBlockEventBase implements IEventInterrupter {
	
	public static PlayerBlockEvent Activated(EntityPlayer player, Block block, int x, int y, int z) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.ACTIVATED, player, x, y, z);
		event.block = block;
		return event;
	}

	public static PlayerBlockEvent ActivationAttempt(EntityPlayer player, Block block, int x, int y, int z, int direction, float xOffset, float yOffset, float zOffset) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.ACTIVATION_ATTEMPT, player, x, y, z);
		event.direction = direction;
		event.xOffset = xOffset;
		event.yOffset = yOffset;
		event.zOffset = zOffset;
		event.block = block;
		return event;
	}
	
	public static PlayerBlockEvent PlaceAttempt(EntityPlayer player, ItemStack itemStack, int x, int y, int z, int direction, float xOffset, float yOffset, float zOffset) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.PLACE_ATTEMPT, player, x, y, z);
		event.itemStack = itemStack;
		event.direction = direction;
		event.xOffset = xOffset;
		event.yOffset = yOffset;
		event.zOffset = zOffset;
		return event;
	}

	public static PlayerBlockEvent RemoveAttempt(EntityPlayer player, int x, int y, int z) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.REMOVE_ATTEMPT, player, x, y, z);
		return event;
	}

	public static PlayerBlockEvent CheckCanPlayerEdit(EntityPlayer player, int x, int y, int z, int direction, ItemStack itemStack) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.CHECK_PLAYEREDIT, player, x, y, z);
		event.direction = direction;
		event.itemStack = itemStack;
		return event;
	}
	
	public enum TYPE { ACTIVATED, ACTIVATION_ATTEMPT, PLACE_ATTEMPT, REMOVE_ATTEMPT, CHECK_PLAYEREDIT };
	
	private TYPE type;
	private boolean isHandled = false;
	private boolean isAllowed = true;

	protected ItemStack itemStack = null;
	protected int direction = -1;
	protected float xOffset = -1F;
	protected float yOffset = -1F;
	protected float zOffset = -1F;
	
	public TYPE getType() {
		return type;
	}
	
	public boolean isHandled() {
		return (type == TYPE.ACTIVATION_ATTEMPT || type == TYPE.PLACE_ATTEMPT || type == TYPE.REMOVE_ATTEMPT) && isHandled;
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
	
	private PlayerBlockEvent(TYPE type, EntityPlayer player, int x, int y, int z) {
		super(player, player.worldObj, x, y, z);
		this.type = type;
	}

	@Override
	public boolean isInterrupted() {
		return isHandled();
	}
}
