package btwmods.chat;

import btwmods.events.IAPIListener;

public interface IPlayerUsernameListener extends IAPIListener {
	public void onPlayerUsernameAction(PlayerUsernameEvent event);
}
