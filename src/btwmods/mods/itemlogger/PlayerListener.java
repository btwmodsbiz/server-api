package btwmods.mods.itemlogger;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;

import btwmods.IMod;
import btwmods.api.player.PlayerAPI;
import btwmods.api.player.events.ContainerEvent;
import btwmods.api.player.events.DropEvent;
import btwmods.api.player.events.SlotEvent;
import btwmods.api.player.listeners.IContainerListener;
import btwmods.api.player.listeners.IDropListener;
import btwmods.api.player.listeners.ISlotListener;

public class PlayerListener implements IMod, ISlotListener, IDropListener, IContainerListener {
	
	private PlayerAPI api;
	private Logger logger;

	@Override
	public void init() {
		logger = ItemLogger.GetLogger();
		PlayerAPI.addListener(this);
	}

	@Override
	public void unload() {
		PlayerAPI.removeListener(this);
	}

	@Override
	public void containerAction(ContainerEvent event) {
		EntityPlayer player = event.getPlayer();
		if (event.getType() == ContainerEvent.TYPE.OPENED) {
			logger.log(Level.INFO, player.username + " at " + (int)player.posX + "/" + (int)player.posY + "/" + (int)player.posZ + " opened " + event.getBlock().getBlockName() + " at " + event.getX() + "/" + event.getY() + "/" + event.getZ(),
					new Object[] { "opened container", event });
		}
		else if (event.getType() == ContainerEvent.TYPE.REMOVED) {
			logger.log(Level.INFO, player.username + " at " + (int)player.posX + "/" + (int)player.posY + "/" + (int)player.posZ + " removed " + event.getBlock().getBlockName() + " at " + event.getX() + "/" + event.getY() + "/" + event.getZ(),
					new Object[] { "removed container", event });
		}
		else if (event.getType() == ContainerEvent.TYPE.PLACED) {
			
		}
	}

	@Override
	public void dropAction(DropEvent event) {
		
	}

	@Override
	public void slotAction(SlotEvent event) {
		ItemStack withdrawn = null;
		int withdrawnQuantity = -1;
		
		ItemStack deposited = null;
		int depositedQuantity = -1;
		
		if (event.getType() == SlotEvent.TYPE.ADD) {
			if (event.slotIsContainer()) {
				deposited = event.getSlotItems();
				depositedQuantity = event.getQuantity();
			}
		}
		else if (event.getType() == SlotEvent.TYPE.REMOVE) {
			if (event.slotIsContainer()) {
				withdrawn = event.getHeldItems();
				withdrawnQuantity = event.getQuantity();
			}
		}
		else if (event.getType() == SlotEvent.TYPE.SWITCH) {
			if (event.slotIsContainer()) {
				withdrawn = event.getHeldItems();
				deposited = event.getSlotItems();
			}
			else {
				withdrawn = event.getSlotItems();
				deposited = event.getHeldItems();
			}
			
			withdrawnQuantity = withdrawn.stackSize;
			depositedQuantity = deposited.stackSize;
		}
		else if (event.getType() == SlotEvent.TYPE.TRANSFER) {
			if (event.getSlot().inventory instanceof InventoryPlayer) {
				deposited = event.getOriginalItems();
				depositedQuantity = event.getQuantity();
			}
			else {
				withdrawn = event.getOriginalItems();
				withdrawnQuantity = event.getQuantity();
			}
		}
		else {
			// TODO: use proper logging.
			ItemLogger.GetLogger().log(Level.SEVERE, "Unknown slotAction: " + event.getType().toString());
		}
		
		EntityPlayer player = event.getPlayer();
		
		if (withdrawn != null)
			logger.log(Level.INFO, player.username + " at " + (int)player.posX + "/" + (int)player.posY + "/" + (int)player.posZ + " withdrew " + withdrawnQuantity + " " + withdrawn.getItemName() + " from " + event.getContainer().getClass().getSimpleName() + " (" + event.getSlot().inventory.getInvName() + ")",
					new Object[] { "withdrawn", event, withdrawn, withdrawnQuantity });
		
		if (deposited != null)
			logger.log(Level.INFO, player.username + " at " + (int)player.posX + "/" + (int)player.posY + "/" + (int)player.posZ + " deposited " + depositedQuantity + " " + deposited.getItemName() + " into " + event.getContainer().getClass().getSimpleName() + " (" + event.getSlot().inventory.getInvName() + ")",
					new Object[] { "deposited", event, deposited, depositedQuantity });
	}
}
