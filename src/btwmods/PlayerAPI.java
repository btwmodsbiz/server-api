package btwmods;

import btwmods.events.EventDispatcher;
import btwmods.events.EventDispatcherFactory;
import btwmods.events.IAPIListener;
import btwmods.player.BlockEvent;
import btwmods.player.ContainerEvent;
import btwmods.player.DropEvent;
import btwmods.player.IActionListener;
import btwmods.player.IBlockListener;
import btwmods.player.IContainerListener;
import btwmods.player.IDropListener;
import btwmods.player.IInstanceListener;
import btwmods.player.ISlotListener;
import btwmods.player.InstanceEvent;
import btwmods.player.InstanceEvent.METADATA;
import btwmods.player.InvocationWrapper;
import btwmods.player.PlayerActionEvent;
import btwmods.player.RespawnPosition;
import btwmods.player.SlotEvent;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.Container;
import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Slot;
import net.minecraft.src.World;

public class PlayerAPI {
	private static EventDispatcher listeners = EventDispatcherFactory.create(new Class[] { IInstanceListener.class, IBlockListener.class, ISlotListener.class,
			IContainerListener.class, IDropListener.class }, new InvocationWrapper());
	
	private PlayerAPI() {}
	
	public static void addListener(IAPIListener listener) {
		listeners.addListener(listener);
	}

	public static void removeListener(IAPIListener listener) {
		listeners.removeListener(listener);
	}

	public static boolean blockActivationAttempt(int blockId, World world, int x, int y, int z, EntityPlayer player, int direction, float xOffset, float yOffset, float zOffset) {
		boolean activated;
		
		if (!listeners.isEmpty(IBlockListener.class)) {
			BlockEvent event = BlockEvent.ActivationAttempt(player, Block.blocksList[blockId], x, y, z, direction, xOffset, yOffset, zOffset);
			((IBlockListener)listeners).blockAction(event);
			activated = event.isHandled();
		}
		
		activated = Block.blocksList[blockId].onBlockActivated(world, x, y, z, player, direction, xOffset, yOffset, zOffset);
		
		if (activated) {
			blockActivated(player, Block.blocksList[blockId], x, y, z);
		}
		
		return activated;
	}
	
	/**
	 * Handle a successful block activation.
	 * 
	 * @param player The player that activated the block.
	 * @param block The block that was activated.
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 * @see net.minecraft.src.ItemInWorldManager#activateBlockOrUseItem(EntityPlayer, World, ItemStack, int, int, int, int, float, float, float)
	 */
	public static void blockActivated(EntityPlayer player, Block block, int x, int y, int z) {
		if (!listeners.isEmpty(IBlockListener.class)) {
			BlockEvent event = BlockEvent.Activated(player, block, x, y, z);
			((IBlockListener)listeners).blockAction(event);
		}

		if (block instanceof BlockContainer) {
			containerOpened(player, block, player.craftingInventory, x, y, z);
		}
	}
	
	public static boolean blockPlaceAttempt(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int direction, float xOffset, float yOffset, float zOffset) {
		if (!listeners.isEmpty(IBlockListener.class)) {
			BlockEvent event = BlockEvent.PlaceAttempt(player, itemStack, x, y, z, direction, xOffset, yOffset, zOffset);
			((IBlockListener)listeners).blockAction(event);
			
			if (event.isHandled())
				return event.isAllowed();
		}
		
		return itemStack.tryPlaceItemIntoWorld(player, world, x, y, z, direction, xOffset, yOffset, zOffset);
	}
	
	public static void blockRemoved(EntityPlayer player, Block block, int metadata, int x, int y, int z) {
		if (block instanceof BlockContainer && !listeners.isEmpty(IContainerListener.class)) {
			ContainerEvent event = ContainerEvent.Removed(player, block, metadata, x, y, z);
			((IContainerListener)listeners).containerAction(event);
		}
	}
	
	@SuppressWarnings("unused")
	public static void containerPlaced(EntityPlayer player, Container container, World world, int x, int y, int z) {
		//TODO: this.items.containerPlaced(player, container, world, x, y, z);
	}
	
	public static void containerOpened(EntityPlayer player, Block block, Container container, int x, int y, int z) {
		if (!listeners.isEmpty(IContainerListener.class)) {
			ContainerEvent event = ContainerEvent.Open(player, block, container, x, y, z);
			((IContainerListener)listeners).containerAction(event);
		}
	}
	
	/**
	 * Player purposefully drops an item.
	 * 
	 * @param player The player that dropped the item.
	 * @param itemStack The items that were dropped.
	 * @param mouseButton The mouse button that was used: 0 for left, 1 for right.
	 */
	public static void itemDropped(EntityPlayer player, ItemStack itemStack, int mouseButton) {
		if (!listeners.isEmpty(IDropListener.class)) {
			DropEvent event;
			
			if (mouseButton == 1)
				event = DropEvent.One(player, itemStack);
			else
				event = DropEvent.Stack(player, itemStack);

        	((IDropListener)listeners).dropAction(event);
		}
	}
	
	/**
	 * An item is dropped from a player's inventory for any reason (purposefully drops an item or closed a crafting window).
	 * 
	 * @param player The player that ejected the items.
	 * @param items The items ejected.
	 */
	public static void itemEjected(EntityPlayer player, ItemStack items) {
		if (!listeners.isEmpty(IDropListener.class)) {
			DropEvent event = DropEvent.Eject(player, items);
        	((IDropListener)listeners).dropAction(event);
		}
	}
	
	/**
	 * All player items are dropped.
	 * 
	 * @param player The player that dropped the items.
	 */
	public static void itemsDroppedAll(EntityPlayer player) {
		if (!listeners.isEmpty(IDropListener.class)) {
			DropEvent event = DropEvent.All(player);
        	((IDropListener)listeners).dropAction(event);
		}
	}
	
	public static void itemTransfered(EntityPlayer player, Container container, int slotId, ItemStack original) {
		if (!listeners.isEmpty(ISlotListener.class)) {
        	SlotEvent event = SlotEvent.Transfer(player, container, slotId, original);
        	((ISlotListener)listeners).slotAction(event);
		}
	}
	
	public static void itemAddedToSlot(EntityPlayer player, Container container, Slot clickedSlot, int quantity) {
		if (!listeners.isEmpty(ISlotListener.class)) {
        	SlotEvent event = SlotEvent.Add(player, container, clickedSlot, quantity);
        	((ISlotListener)listeners).slotAction(event);
		}
	}
	
	public static void itemRemovedFromSlot(EntityPlayer player, Container container, Slot clickedSlot, int quantity) {
		if (!listeners.isEmpty(ISlotListener.class)) {
        	SlotEvent event = SlotEvent.Remove(player, container, clickedSlot, quantity);
        	((ISlotListener)listeners).slotAction(event);
		}
	}
	
	public static void itemSwitchedWithSlot(EntityPlayer player, Container container, Slot clickedSlot) {
		if (!listeners.isEmpty(ISlotListener.class)) {
        	SlotEvent event = SlotEvent.Switch(player, container, clickedSlot);
        	((ISlotListener)listeners).slotAction(event);
		}
	}
	
	public static void login(EntityPlayer player) {
		if (!listeners.isEmpty(IInstanceListener.class)) {
        	InstanceEvent event = InstanceEvent.Login(player);
        	((IInstanceListener)listeners).instanceAction(event);
		}
	}
	
	public static void logout(EntityPlayer player) {
		if (!listeners.isEmpty(IInstanceListener.class)) {
        	InstanceEvent event = InstanceEvent.Logout(player);
        	((IInstanceListener)listeners).instanceAction(event);
		}
	}

	/**
	 * @param oldPlayerInstance The old instance of {@link EntityPlayer} that is being recreated.
	 * @return The position the player should respawn at, if set by a mod. <code>null</code> otherwise.
	 */
	public static RespawnPosition handleRespawn(EntityPlayer oldPlayerInstance) {
		if (!listeners.isEmpty(IInstanceListener.class)) {
        	InstanceEvent event = InstanceEvent.Respawn(oldPlayerInstance);
        	((IInstanceListener)listeners).instanceAction(event);
        	return event.getRespawnPosition();
		}
		
		return null;
	}
	
	public static boolean isPvPEnabled(EntityPlayer player) {
		if (!listeners.isEmpty(IInstanceListener.class)) {
        	InstanceEvent event = InstanceEvent.CheckMetadata(player, METADATA.IS_PVP);
        	((IInstanceListener)listeners).instanceAction(event);
        	if (event.isInterrupted()) {
        		return event.getMetadataBooleanValue();
        	}
		}
		
		return MinecraftServer.getServer().isPVPEnabled();
	}
	
	public static void readFromNBT(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		if (!listeners.isEmpty(IInstanceListener.class)) {
        	InstanceEvent event = InstanceEvent.ReadFromNBT(player, nbtTagCompound);
        	((IInstanceListener)listeners).instanceAction(event);
		}
	}
	
	public static void writeToNBT(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		if (!listeners.isEmpty(IInstanceListener.class)) {
        	InstanceEvent event = InstanceEvent.WriteToNBT(player, nbtTagCompound);
        	((IInstanceListener)listeners).instanceAction(event);
		}
	}

	public static void onPlayerAttack(EntityLiving attackedEntity, DamageSource source) {
		if (!listeners.isEmpty(IActionListener.class)) {
			PlayerActionEvent event = PlayerActionEvent.Attack(attackedEntity, source);
        	((IActionListener)listeners).onPlayerAction(event);
		}
	}
	
	public static void onPlayerMetadataChanged(EntityPlayer player, InstanceEvent.METADATA metadata) {
		if (!listeners.isEmpty(IInstanceListener.class)) {
        	InstanceEvent event = InstanceEvent.MetadataChanged(player, metadata);
        	((IInstanceListener)listeners).instanceAction(event);
		}
	}
	
	public static void onPlayerMetadataChanged(EntityPlayer player, InstanceEvent.METADATA metadata, Object newValue) {
		if (!listeners.isEmpty(IInstanceListener.class)) {
        	InstanceEvent event = InstanceEvent.MetadataChanged(player, metadata, newValue);
        	((IInstanceListener)listeners).instanceAction(event);
		}
	}
}
