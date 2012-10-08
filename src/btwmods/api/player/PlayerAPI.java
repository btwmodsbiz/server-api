package btwmods.api.player;

import java.awt.event.ItemListener;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Properties;

import btwmods.ModLoader;
import btwmods.ModProperties;
import btwmods.api.player.listeners.IContainerListener;
import btwmods.api.player.listeners.IDropListener;
import btwmods.api.player.listeners.ISlotListener;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.World;

public class PlayerAPI {
	public EntityPlayer player;
	
	private static ModLoader<PlayerAPI,IPlayerAPIMod> ModLoader = new ModLoader<PlayerAPI,IPlayerAPIMod>(PlayerAPI.class, IPlayerAPIMod.class);
	private ModLoader.Mods mods;
	
	private HashSet<ISlotListener> slotListeners = new HashSet<ISlotListener>();
	private HashSet<IContainerListener> containerListeners = new HashSet<IContainerListener>();
	private HashSet<IDropListener> dropListeners = new HashSet<IDropListener>();
	
	public final static String MOD_LIST_KEY = "PlayerAPI.Mods";
	
	public static void SetDefaultProperties(Properties properties) {
		ModLoader.setDefaultProperties(properties, ModProperties.Get(MOD_LIST_KEY));
	}
	
	public PlayerAPI(EntityPlayer player) {
		this.player = player;
		mods = ModLoader.createMods(ModProperties.Get(MOD_LIST_KEY));
		mods.initMods(this);
	}
	
	public void unload() {
		mods.unloadMods();
	}
	
	public void addListener(EventListener listener) {
		if (listener instanceof ISlotListener)
			slotListeners.add((ISlotListener)listener);
		else if (listener instanceof IContainerListener)
			containerListeners.add((IContainerListener)listener);
		else if (listener instanceof IDropListener)
			dropListeners.add((IDropListener)listener);
	}

	public void removeListener(EventListener listener) {
		if (listener instanceof ISlotListener)
			slotListeners.remove((ISlotListener)listener);
		else if (listener instanceof IContainerListener)
			containerListeners.remove((IContainerListener)listener);
		else if (listener instanceof IDropListener)
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
	public void activatedBlock(World world, ItemStack heldItemStack, Block block, int x, int y, int z) {
		/*
		 * BlockContainers checked:
		 * - O C BrewingStand
		 * - O C Chest
		 * - O C Dispenser
		 * - O C EnchantmentTable
		 * - O C EnderChest
		 * - O C Furance
		 * - O C JukeBox (opens as ContainerPlayer. does not close)
		 * - O C Note (opens as ContainerPlayer. does not close)
		 * - O C PistonMoving (does not trigger #activatedBlock)
		 * - O C FCAnvil
		 * - O C BlockDispenser
		 * - O C Hopper
		 * - O C CookingVessel (use Block to determine sub-type)
		 * - O C InfernalEnchanter
		 * - O C Millstone
		 * - O C Pully
		 * - O C Turntable (opens as ContainerPlayer. does not close)
		 * - O C Vase (opens as ContainerPlayer only on deposit. does not close)
		 */

		if (block instanceof BlockContainer) {
			containerOpened(block, this.player.craftingInventory, world, x, y, z);
		}
		else {
			// TODO: remove after testing.
			MinecraftServer.logger.info("Activated non-container block " + block.getBlockName() + "(" + block.blockID + ")");
		}
	}
	

	public void containerPlaced(Container container, World world, int x, int y, int z) {
		//this.items.containerPlaced(player, container, world, x, y, z);
	}
	
	public void containerOpened(Block block, Container container, World world, int x, int y, int z) {
		if (!containerListeners.isEmpty())
			for (IContainerListener listener : containerListeners)
				listener.containerOpened(block, container, world, x, y, z);
	}
	
	public void containerClosed(Container container) {
		if (!containerListeners.isEmpty())
			for (IContainerListener listener : containerListeners)
				listener.containerClosed(container);
	}
	
	public void containerDestroyed(Container container, int x, int y, int z) {
		//this.items.containerDestroyed(player, container, x, y, z);
	}
	
	/**
	 * Player purposefully drops an item.
	 * @param itemStack
	 * @param mouseButton
	 */
	public void itemDropped(ItemStack itemStack, int mouseButton) {
		if (!dropListeners.isEmpty()) {
        	ItemStack eventStack = itemStack.copy();
        	if (mouseButton == 1) eventStack.stackSize = 1;
        	
        	itemDropped(eventStack);
		}
	}
	
	/**
	 * An item is dropped from a player's inventory for any reason (e.g. closed a crafting window).
	 * @param items
	 */
	public void itemDropped(ItemStack items) {
		if (!dropListeners.isEmpty())
			for (IDropListener listener : dropListeners)
				listener.itemDropped(items);
	}
	
	/**
	 * All player items are dropped.
	 */
	public void itemsDroppedAll() {
		if (!dropListeners.isEmpty())
			for (IDropListener listener : dropListeners)
				listener.itemsDroppedAll();
	}
	
	/*public void itemWithdrawn(Container container, int slotId, ItemStack itemStack) {
		if (!listeners.isEmpty())
			for (IItemListener listener : listeners)
				listener.itemWithdrawn(container, slotId, itemStack);
	}
	
	public void itemDeposited(Container container, int slotId, ItemStack itemStack) {if (!listeners.isEmpty())
		if (!listeners.isEmpty())
			for (IItemListener listener : listeners)
				listener.itemDeposited(container, slotId, itemStack);
	}*/
	
	public void itemTransfered(Container container, int slotId, ItemStack original) {
		if (!slotListeners.isEmpty()) {
			Slot clickedSlot = (Slot)container.inventorySlots.get(slotId);
			ItemStack remaining = clickedSlot.getStack();

			for (ISlotListener listener : slotListeners)
				listener.itemTransfered(container, clickedSlot, original, remaining);
		}
	}
	
	public void itemAddedToSlot(Container container, Slot clickedSlot, int quantity) {
		if (!slotListeners.isEmpty()) {
			ItemStack slotItems = clickedSlot.getStack();
			ItemStack heldItems = this.player.inventory.getItemStack();

			for (ISlotListener listener : slotListeners)
				listener.itemAddedToSlot(container, clickedSlot, slotItems, heldItems, quantity);
		}
	}
	
	public void itemRemovedFromSlot(Container container, Slot clickedSlot, int quantity) {
		if (!slotListeners.isEmpty()) {
			ItemStack slotItems = clickedSlot.getStack();
			ItemStack heldItems = this.player.inventory.getItemStack();

			for (ISlotListener listener : slotListeners)
				listener.itemRemovedFromSlot(container, clickedSlot, slotItems, heldItems, quantity);
		}
	}
	
	public void itemSwitchedWithSlot(Container container, Slot clickedSlot) {
		if (!slotListeners.isEmpty()) {
			ItemStack slotItems = clickedSlot.getStack();
			ItemStack heldItems = this.player.inventory.getItemStack();

			for (ISlotListener listener : slotListeners)
				listener.itemSwitchedWithSlot(container, clickedSlot, slotItems, heldItems);
		}
	}
}
