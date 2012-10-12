package btwmods.player.listeners;

import btwmods.IAPIListener;
import btwmods.player.events.SlotEvent;

public interface ISlotListener extends IAPIListener {
	public void slotAction(SlotEvent event);
}
