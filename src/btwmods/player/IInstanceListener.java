package btwmods.player;

import btwmods.events.IAPIListener;

public interface IInstanceListener extends IAPIListener {
	void instanceAction(InstanceEvent event);
}
