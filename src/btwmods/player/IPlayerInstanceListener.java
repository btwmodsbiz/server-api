package btwmods.player;

import btwmods.events.IAPIListener;

public interface IPlayerInstanceListener extends IAPIListener {
	void instanceAction(PlayerInstanceEvent event);
}
