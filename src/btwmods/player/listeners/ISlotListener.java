package btwmods.player.listeners;

import java.util.EventListener;

import btwmods.player.events.SlotEvent;

public interface ISlotListener extends EventListener {
	public void slotAction(SlotEvent event);
}
