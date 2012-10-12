package btwmods.player.listeners;

import btwmods.IAPIListener;
import btwmods.player.events.InstanceEvent;

public interface IInstanceListener extends IAPIListener {
	void instanceAction(InstanceEvent event);
}
