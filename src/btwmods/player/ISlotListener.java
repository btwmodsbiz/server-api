package btwmods.player;

import btwmods.events.IAPIListener;

public interface ISlotListener extends IAPIListener {
	public void slotAction(SlotEvent event);
}
