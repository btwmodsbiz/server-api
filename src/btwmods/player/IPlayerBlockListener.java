package btwmods.player;

import btwmods.events.IAPIListener;

public interface IPlayerBlockListener extends IAPIListener {
	public void blockAction(PlayerBlockEvent event);
}
