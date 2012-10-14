package btwmods.player;

import btwmods.IAPIListener;

public interface IInstanceListener extends IAPIListener {
	void instanceAction(InstanceEvent event);
}
