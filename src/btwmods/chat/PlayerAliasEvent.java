package btwmods.chat;

import btwmods.events.APIEvent;
import btwmods.events.IEventInterrupter;

public class PlayerAliasEvent extends APIEvent implements IEventInterrupter {
	
	public final String username;
	public String alias = null;

	public PlayerAliasEvent(String username) {
		super(username);
		this.username = username;
	}

	@Override
	public boolean isInterrupted() {
		return alias != null;
	}
}
