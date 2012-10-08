package btwmods.api.player.listeners;

import java.util.EventListener;
import btwmods.api.player.events.DropEvent;

public interface IDropListener extends EventListener {
	void dropAction(DropEvent event);
}
