package btwmods.player;

import btwmods.events.APIEvent;
import net.minecraft.src.Container;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryEnderChest;
import net.minecraft.src.InventoryLargeChest;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.TileEntity;

public class SlotEvent extends APIEvent {
	
	public enum TYPE { ADD, REMOVE, SWITCH, TRANSFER };

	private TYPE type;
	private EntityPlayer player;
	private Container container;
	private int slotId = -1;
	private Slot slot = null;
	private ItemStack slotItems = null;
	private ItemStack heldItems = null;
	private ItemStack originalItems = null;
	private ItemStack remainingItems = null;
	private int quantity = -1;
	
	public TYPE getType() {
		return type;
	}
	
	public EntityPlayer getPlayer() {
		return player;
	}
	
	public Container getContainer() {
		return container;
	}
	
	public boolean slotIsContainer() {
		IInventory inventory = getSlot().inventory;
		return inventory instanceof TileEntity || inventory instanceof Entity || inventory instanceof InventoryLargeChest || inventory instanceof InventoryEnderChest;
	}
	
	public int getSlotId() {
		if (slotId == -1)
			slotId = slot.slotNumber;
		
		return slotId;
	}
	
	public Slot getSlot() {
		if (slot == null)
			slot = container.getSlot(slotId);
		
		return slot;
	}
	
	public ItemStack getSlotItems() {
		if (slotItems == null && type != TYPE.TRANSFER)
			slotItems = getSlot().getStack();
		
		return slotItems;
	}
	
	public ItemStack getHeldItems() {
		if (heldItems == null && type != TYPE.TRANSFER)
			heldItems = player.inventory.getItemStack();
		
		return heldItems;
	}
	
	public ItemStack getOriginalItems() {
		return originalItems;
	}
	
	public ItemStack getRemainingItems() {
		if (remainingItems == null && type == TYPE.TRANSFER)
			remainingItems = getSlot().getStack();
		
		return remainingItems;
	}
	
	public int getQuantity() {
		if (quantity == -1 && type == TYPE.TRANSFER)
			quantity = getOriginalItems().stackSize - (getRemainingItems() == null ? 0 : getRemainingItems().stackSize);
		
		return quantity;
	}
	
	public static SlotEvent Add(EntityPlayer player, Container container, Slot slot, int quantity) {
		SlotEvent event = new SlotEvent(TYPE.ADD, player, container, slot.slotNumber, slot);
		event.quantity = quantity;
		return event;
	}
	
	public static SlotEvent Remove(EntityPlayer player, Container container, Slot slot, int quantity) {
		SlotEvent event = new SlotEvent(TYPE.REMOVE, player, container, slot.slotNumber, slot);
		event.quantity = quantity;
		return event;
	}
	
	public static SlotEvent Switch(EntityPlayer player, Container container, Slot slot) {
		SlotEvent event = new SlotEvent(TYPE.SWITCH, player, container, slot.slotNumber, slot);
		return event;
	}
	
	public static SlotEvent Transfer(EntityPlayer player, Container container, int slotId, ItemStack originalItems) {
		SlotEvent event = new SlotEvent(TYPE.TRANSFER, player, container, slotId, null);
		event.originalItems = originalItems;
		return event;
	}
	
	private SlotEvent(TYPE type, EntityPlayer player, Container container, int slotId, Slot slot) {
		super(player);
		this.type = type;
		this.player = player;
		this.container = container;
		this.slotId = slotId;
		this.slot = slot;
	}
}
