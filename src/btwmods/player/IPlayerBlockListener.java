package btwmods.player;

import btwmods.events.IAPIListener;

public interface IPlayerBlockListener extends IAPIListener {
	public void onPlayerBlockAction(PlayerBlockEvent event);
}
