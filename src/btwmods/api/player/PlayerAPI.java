package btwmods.api.player;

import java.util.EventListener;
import java.util.HashSet;

import btwmods.api.player.events.BlockEvent;
import btwmods.api.player.events.ContainerEvent;
import btwmods.api.player.events.DropEvent;
import btwmods.api.player.events.SlotEvent;
import btwmods.api.player.listeners.IBlockListener;
import btwmods.api.player.listeners.IContainerListener;
import btwmods.api.player.listeners.IDropListener;
import btwmods.api.player.listeners.ISlotListener;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.World;

public class PlayerAPI {
	private static HashSet<IBlockListener> blockListeners = new HashSet<IBlockListener>();
	private static HashSet<ISlotListener> slotListeners = new HashSet<ISlotListener>();
	private static HashSet<IContainerListener> containerListeners = new HashSet<IContainerListener>();
	private static HashSet<IDropListener> dropListeners = new HashSet<IDropListener>();
	
	private PlayerAPI() {}
	
	public static void addListener(EventListener listener) {
		if (listener instanceof IBlockListener)
			blockListeners.add((IBlockListener)listener);
		if (listener instanceof ISlotListener)
			slotListeners.add((ISlotListener)listener);
		if (listener instanceof IContainerListener)
			containerListeners.add((IContainerListener)listener);
		if (listener instanceof IDropListener)
			dropListeners.add((IDropListener)listener);
	}

	public static void removeListener(EventListener listener) {
		if (listener instanceof IBlockListener)
			blockListeners.remove((IBlockListener)listener);
		if (listener instanceof ISlotListener)
			slotListeners.remove((ISlotListener)listener);
		if (listener instanceof IContainerListener)
			containerListeners.remove((IContainerListener)listener);
		if (listener instanceof IDropListener)
			dropListeners.remove((IDropListener)listener);
	}
	
	/**
	 * Handle a successful block activation.
	 * 
	 * @see net.minecraft.src.ItemInWorldManager#activateBlockOrUseItem(EntityPlayer, World, ItemStack, int, int, int, int, float, float, float)
	 * @param world
	 * @param heldItemStack
	 * @param block
	 * @param x coordinate of the block
	 * @param y coordinate of the block
	 * @param z coordinate of the block
	 */
	public static void activatedBlock(EntityPlayer player, Block block, int x, int y, int z) {
		if (!blockListeners.isEmpty()) {
			BlockEvent event = BlockEvent.Activated(player, block, x, y, z);
			
			for (IBlockListener listener : blockListeners)
				listener.blockActivated(event);
		}

		if (block instanceof BlockContainer) {
			containerOpened(player, block, player.craftingInventory, x, y, z);
		}
	}
	
	public static void blockRemoved(EntityPlayer player, Block block, int metadata, int x, int y, int z) {
		if (block instanceof BlockContainer && !containerListeners.isEmpty()) {
			ContainerEvent event = ContainerEvent.Removed(player, block, metadata, x, y, z);
			
			for (IContainerListener listener : containerListeners)
				listener.containerAction(event);
		}
	}
	
	public static void containerPlaced(EntityPlayer player, Container container, World world, int x, int y, int z) {
		//TODO: this.items.containerPlaced(player, container, world, x, y, z);
	}
	
	public static void containerOpened(EntityPlayer player, Block block, Container container, int x, int y, int z) {
		if (!containerListeners.isEmpty()) {
			ContainerEvent event = ContainerEvent.Open(player, block, container, x, y, z);
			
			for (IContainerListener listener : containerListeners)
				listener.containerAction(event);
		}
	}
	
	/**
	 * Player purposefully drops an item.
	 * @param itemStack
	 * @param mouseButton
	 */
	public static void itemDropped(EntityPlayer player, ItemStack itemStack, int mouseButton) {
		if (!dropListeners.isEmpty()) {
			DropEvent event;
			
			if (mouseButton == 1)
				event = DropEvent.One(player, itemStack);
			else
				event = DropEvent.Stack(player, itemStack);
			
			for (IDropListener listener : dropListeners)
				listener.dropAction(event);
		}
	}
	
	/**
	 * An item is dropped from a player's inventory for any reason (purposefully drops an item or closed a crafting window).
	 * @param items
	 */
	public static void itemEjected(EntityPlayer player, ItemStack items) {
		if (!dropListeners.isEmpty()) {
			DropEvent event = DropEvent.Eject(player, items);
		
			for (IDropListener listener : dropListeners)
				listener.dropAction(event);
		}
	}
	
	/**
	 * All player items are dropped.
	 */
	public static void itemsDroppedAll(EntityPlayer player) {
		if (!dropListeners.isEmpty()) {
			DropEvent event = DropEvent.All(player);
			
			for (IDropListener listener : dropListeners)
				listener.dropAction(event);
		}
	}
	
	public static void itemTransfered(EntityPlayer player, Container container, int slotId, ItemStack original) {
		if (!slotListeners.isEmpty()) {
        	SlotEvent event = SlotEvent.Transfer(player, container, slotId, original);
        	
			for (ISlotListener listener : slotListeners)
				listener.slotAction(event);
		}
	}
	
	public static void itemAddedToSlot(EntityPlayer player, Container container, Slot clickedSlot, int quantity) {
		if (!slotListeners.isEmpty()) {
        	SlotEvent event = SlotEvent.Add(player, container, clickedSlot, quantity);
        	
			for (ISlotListener listener : slotListeners)
				listener.slotAction(event);
		}
	}
	
	public static void itemRemovedFromSlot(EntityPlayer player, Container container, Slot clickedSlot, int quantity) {
		if (!slotListeners.isEmpty()) {
        	SlotEvent event = SlotEvent.Remove(player, container, clickedSlot, quantity);
        	
			for (ISlotListener listener : slotListeners)
				listener.slotAction(event);
		}
	}
	
	public static void itemSwitchedWithSlot(EntityPlayer player, Container container, Slot clickedSlot) {
		if (!slotListeners.isEmpty()) {
        	SlotEvent event = SlotEvent.Switch(player, container, clickedSlot);
        	
			for (ISlotListener listener : slotListeners)
				listener.slotAction(event);
		}
	}
	
	public static void login(EntityPlayer player) {
		// TODO: 
	}
	
	public static void logout(EntityPlayer player) {
		// TODO: 
	}
}
