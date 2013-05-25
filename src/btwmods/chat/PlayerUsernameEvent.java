package btwmods.chat;

import btwmods.events.APIEvent;
import btwmods.events.IEventInterrupter;

public class PlayerUsernameEvent extends APIEvent implements IEventInterrupter {
	
	public enum TYPE { GET_ALIAS, GET_FORMATTED };
	
	public final TYPE type;
	public final String username;

	protected boolean isHandled = false;
	protected String alias = null;
	protected String formattedUsername = null; 
	
	public boolean isHandled() {
		return isHandled;
	}
	
	public String getFormatted() {
		return formattedUsername;
	}
	
	public void setFormatted(String formattedUsername) {
		if (formattedUsername != null && type == TYPE.GET_FORMATTED) {
			this.formattedUsername = formattedUsername;
			isHandled = true;
		}
	}
	
	public String getAlias() {
		return alias;
	}
	
	public void setAlias(String alias) {
		if (alias != null && type == TYPE.GET_ALIAS) {
			this.alias = alias;
			isHandled = true;
		}
	}
	
	public static PlayerUsernameEvent GetFormatted(String username) {
		return new PlayerUsernameEvent(TYPE.GET_FORMATTED, username);
	}
	
	public static PlayerUsernameEvent GetAlias(String username) {
		return new PlayerUsernameEvent(TYPE.GET_ALIAS, username);
	}

	private PlayerUsernameEvent(TYPE type, String username) {
		super(username);
		this.type = type;
		this.username = username;
	}

	@Override
	public boolean isInterrupted() {
		return isHandled();
	}
}
