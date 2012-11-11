package btwmods.player;

import btwmods.events.IAPIListener;

public interface IPlayerActionListener extends IAPIListener {
	public void onPlayerAction(PlayerActionEvent event);
}
