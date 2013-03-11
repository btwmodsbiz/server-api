package btwmods.player;

import btwmods.events.IEventInterrupter;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryEnderChest;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class PlayerBlockEvent extends PlayerBlockEventBase implements IEventInterrupter {
	
	public static PlayerBlockEvent Activated(EntityPlayer player, Block block, int x, int y, int z) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.ACTIVATED, player, player.worldObj, x, y, z);
		event.setBlock(block);
		return event;
	}

	public static PlayerBlockEvent ActivationAttempt(EntityPlayer player, Block block, int x, int y, int z, int direction, float xOffset, float yOffset, float zOffset) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.ACTIVATION_ATTEMPT, player, player.worldObj, x, y, z);
		event.direction = direction;
		event.xOffset = xOffset;
		event.yOffset = yOffset;
		event.zOffset = zOffset;
		event.setBlock(block);
		return event;
	}
	
	public static PlayerBlockEvent ItemUseAttempt(EntityPlayer player, ItemStack itemStack, World world, int x, int y, int z, int direction, float xOffset, float yOffset, float zOffset) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.ITEM_USE_ATTEMPT, player, world, x, y, z);
		event.itemStack = itemStack;
		event.direction = direction;
		event.xOffset = xOffset;
		event.yOffset = yOffset;
		event.zOffset = zOffset;
		return event;
	}
	
	public static PlayerBlockEvent ItemUsed(EntityPlayer player, ItemStack itemStack, World world, int x, int y, int z, int direction, float xOffset, float yOffset, float zOffset) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.ITEM_USED, player, world, x, y, z);
		event.itemStack = itemStack;
		event.direction = direction;
		event.xOffset = xOffset;
		event.yOffset = yOffset;
		event.zOffset = zOffset;
		return event;
	}

	public static PlayerBlockEvent RemoveAttempt(EntityPlayer player, World world, Block block, int metadata, int x, int y, int z) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.REMOVE_ATTEMPT, player, world, x, y, z);
		event.setBlock(block);
		event.setMetadata(metadata);
		return event;
	}

	public static PlayerBlockEvent Removed(EntityPlayer player, World world, Block block, int metadata, int x, int y, int z) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.REMOVED, player, world, x, y, z);
		event.setBlock(block);
		event.setMetadata(metadata);
		return event;
	}

	public static PlayerBlockEvent ItemUseCheckEdit(EntityPlayer player, World world, int x, int y, int z, int direction, ItemStack itemStack) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.ITEM_USE_CHECK_EDIT, player, world, x, y, z);
		event.direction = direction;
		event.itemStack = itemStack;
		return event;
	}

	public static PlayerBlockEvent GetEnderChestInventory(EntityPlayer player, int x, int y, int z, int direction, float xOffset, float yOffset, float zOffset) {
		PlayerBlockEvent event = new PlayerBlockEvent(TYPE.GET_ENDERCHEST_INVENTORY, player, player.worldObj, x, y, z);
		event.direction = direction;
		event.xOffset = xOffset;
		event.yOffset = yOffset;
		event.zOffset = zOffset;
		return event;
	}
	
	public enum TYPE {
		ACTIVATION_ATTEMPT(true),
		ACTIVATED,
		
		REMOVE_ATTEMPT(true),
		REMOVED,

		ITEM_USE_ATTEMPT(true),
		ITEM_USE_CHECK_EDIT,
		ITEM_USED,
		
		GET_ENDERCHEST_INVENTORY(true);
		
		public final boolean allowHandle;
		
		private TYPE() {
			this(false);
		}
		
		private TYPE(boolean allowHandle) {
			this.allowHandle = allowHandle;
		}
	};
	
	private TYPE type;
	private boolean isHandled = false;
	private boolean isAllowed = true;

	protected ItemStack itemStack = null;
	protected int direction = -1;
	protected float xOffset = -1F;
	protected float yOffset = -1F;
	protected float zOffset = -1F;
	
	protected InventoryEnderChest enderChestInventory = null;
	
	public TYPE getType() {
		return type;
	}
	
	public boolean isHandled() {
		return isHandled;
	}
	
	public void markHandled() {
		if (type.allowHandle)
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
	
	public InventoryEnderChest getEnderChestInventory() {
		return enderChestInventory;
	}
	
	public void setEnderChestInventory(InventoryEnderChest inventory) {
		enderChestInventory = inventory;
		markHandled();
	}
	
	private PlayerBlockEvent(TYPE type, EntityPlayer player, World world, int x, int y, int z) {
		super(player, world, x, y, z);
		this.type = type;
	}

	@Override
	public boolean isInterrupted() {
		return isHandled();
	}
}
