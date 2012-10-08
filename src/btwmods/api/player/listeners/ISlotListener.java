package btwmods.api.player.listeners;

import java.util.EventListener;
import net.minecraft.src.Container;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public interface ISlotListener extends EventListener {
	public void itemWithdrawn(Container container, int slotId, ItemStack itemStack);
	public void itemDeposited(Container container, int slotId, ItemStack itemStack);
	public void itemTransfered(Container container, Slot clickedSlot, ItemStack original, ItemStack remaining);
	public void itemAddedToSlot(Container container, Slot clickedSlot, ItemStack slotItems, ItemStack heldItems, int quantity);
	public void itemRemovedFromSlot(Container container, Slot clickedSlot, ItemStack slotItems, ItemStack heldItems, int quantity);
	public void itemSwitchedWithSlot(Container container, Slot clickedSlot, ItemStack slotItems, ItemStack heldItems);
}
