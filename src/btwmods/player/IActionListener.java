package btwmods.player;

import btwmods.events.IAPIListener;

public interface IActionListener extends IAPIListener {
	public void onPlayerAction(PlayerActionEvent event);
}
