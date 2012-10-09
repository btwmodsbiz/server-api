package btwmods.mods.itemlogger;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;

import btwmods.api.player.IPlayerAPIMod;
import btwmods.api.player.PlayerAPI;
import btwmods.api.player.events.ContainerEvent;
import btwmods.api.player.events.DropEvent;
import btwmods.api.player.events.SlotEvent;
import btwmods.api.player.listeners.IContainerListener;
import btwmods.api.player.listeners.IDropListener;
import btwmods.api.player.listeners.ISlotListener;
import btwmods.api.world.IWorldAPIMod;

public class PlayerListener implements IPlayerAPIMod, ISlotListener, IDropListener, IContainerListener {
	
	private PlayerAPI api;
	private Logger logger;

	@Override
	public void init(PlayerAPI parent) {
		api = parent;
		logger = ItemLogger.GetLogger();
		api.addListener(this);
	}

	@Override
	public void unload(PlayerAPI parent) {
		parent.removeListener(this);
	}

	@Override
	public void containerAction(ContainerEvent event) {
		
	}

	@Override
	public void dropAction(DropEvent event) {
		
	}

	@Override
	public void slotAction(SlotEvent event) {
		EntityPlayer player = api.player;
		
		ItemStack withdrawn = null;
		int withdrawnQuantity = 0;
		
		ItemStack deposited = null;
		int depositedQuantity = 0;
		
		if (event.getType() == SlotEvent.TYPE.ADD) {
			if (event.getSlot().inventory instanceof InventoryPlayer) {
				deposited = event.getSlotItems();
				depositedQuantity = event.getQuantity();
			}
			else {
				withdrawn = event.getSlotItems();
				withdrawnQuantity = event.getQuantity();
			}
		}
		else if (event.getType() == SlotEvent.TYPE.REMOVE) {
			if (event.getSlot().inventory instanceof InventoryPlayer) {
				withdrawn = event.getSlotItems();
				withdrawnQuantity = event.getQuantity();
			}
			else {
				deposited = event.getSlotItems();
				depositedQuantity = event.getQuantity();
			}
		}
		else if (event.getType() == SlotEvent.TYPE.SWITCH) {
			if (event.getSlot().inventory instanceof InventoryPlayer) {
				withdrawn = event.getSlotItems();
				deposited = event.getHeldItems();
			}
			else {
				withdrawn = event.getHeldItems();
				deposited = event.getSlotItems();
			}
			
			withdrawnQuantity = withdrawn.stackSize;
			depositedQuantity = deposited.stackSize;
		}
		else if (event.getType() == SlotEvent.TYPE.TRANSFER) {
			// TODO: determine the direction of transfer.
		}
		else {
			ItemLogger.GetLogger().log(Level.SEVERE, "Unknown slotAction: " + event.getType().toString());
		}
		
		if (withdrawn != null)
			logger.log(Level.INFO, player.username + " at x" + player.posX + " y" + player.posY + " z" + player.posZ + " withdrew " + withdrawnQuantity + " " + withdrawn.getItemName() + " from " + event.getContainer().getClass().getSimpleName() + " (" + event.getSlot().inventory.getInvName() + ")",
					new Object[] { "withdrawn", event, withdrawn, withdrawnQuantity });
		
		if (deposited != null)
			logger.log(Level.INFO, player.username + " at x" + player.posX + " y" + player.posY + " z" + player.posZ + " deposited " + withdrawnQuantity + " " + withdrawn.getItemName() + " into " + event.getContainer().getClass().getSimpleName() + " (" + event.getSlot().inventory.getInvName() + ")",
					new Object[] { "deposited", event, deposited, depositedQuantity });
	}
}
