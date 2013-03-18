package btwmods.chat;

import btwmods.events.IAPIListener;

public interface IPlayerAliasListener extends IAPIListener {
	public void onPlayerAliasAction(PlayerAliasEvent event);
}
