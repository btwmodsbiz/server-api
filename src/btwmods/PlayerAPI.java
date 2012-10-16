package btwmods;

import btwmods.events.EventDispatcher;
import btwmods.events.EventDispatcherFactory;
import btwmods.events.IAPIListener;
import btwmods.player.BlockEvent;
import btwmods.player.ContainerEvent;
import btwmods.player.DropEvent;
import btwmods.player.IBlockListener;
import btwmods.player.IContainerListener;
import btwmods.player.IDropListener;
import btwmods.player.IInstanceListener;
import btwmods.player.ISlotListener;
import btwmods.player.InstanceEvent;
import btwmods.player.InvocationWrapper;
import btwmods.player.RespawnPosition;
import btwmods.player.SlotEvent;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.Container;
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

	public static boolean blockActivationAttempt(EntityPlayer player, Block block, int x, int y, int z) {
		if (!listeners.isEmpty(IBlockListener.class)) {
			BlockEvent event = BlockEvent.ActivationAttempt(player, block, x, y, z);
			((IBlockListener)listeners).blockActivationAttempt(event);
			return event.isActivationHandled();
		}
		
		return false;
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
			((IBlockListener)listeners).blockActivated(event);
		}

		if (block instanceof BlockContainer) {
			containerOpened(player, block, player.craftingInventory, x, y, z);
		}
	}
	
	public static void blockRemoved(EntityPlayer player, Block block, int metadata, int x, int y, int z) {
		if (block instanceof BlockContainer && !listeners.isEmpty(IContainerListener.class)) {
			ContainerEvent event = ContainerEvent.Removed(player, block, metadata, x, y, z);
			((IContainerListener)listeners).containerAction(event);
		}
	}
	
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
	 * @return true if the event has been handled and no others (e.g. hardcore spawning handler) should be called.
	 */
	public static RespawnPosition handleRespawn(EntityPlayer oldPlayerInstance) {
		if (!listeners.isEmpty(IInstanceListener.class)) {
        	InstanceEvent event = InstanceEvent.Respawn(oldPlayerInstance);
        	((IInstanceListener)listeners).instanceAction(event);
        	return event.getRespawnPosition();
		}
		
		return null;
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
}
