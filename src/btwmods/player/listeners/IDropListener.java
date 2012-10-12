package btwmods.player.listeners;

import btwmods.IAPIListener;
import btwmods.player.events.DropEvent;

public interface IDropListener extends IAPIListener {
	void dropAction(DropEvent event);
}
