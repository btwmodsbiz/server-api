package btwmods.player;

import btwmods.IAPIListener;

public interface ISlotListener extends IAPIListener {
	public void slotAction(SlotEvent event);
}
