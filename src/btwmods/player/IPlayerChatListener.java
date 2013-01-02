package btwmods.player;

import btwmods.events.IAPIListener;

public interface IPlayerChatListener extends IAPIListener {
	public void onPlayerChatAction(PlayerChatEvent event);
}
