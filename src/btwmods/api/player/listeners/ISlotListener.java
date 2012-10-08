package btwmods.api.player.listeners;

import java.util.EventListener;
import btwmods.api.player.events.SlotEvent;

public interface ISlotListener extends EventListener {
	public void slotAction(SlotEvent event);
}
