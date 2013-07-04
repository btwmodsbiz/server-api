package btwmods.chat;

import java.util.List;

import btwmods.ChatAPI;
import btwmods.events.APIEvent;
import btwmods.events.IEventInterrupter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;

public class PlayerChatEvent extends APIEvent implements IEventInterrupter {
	
	public enum TYPE { HANDLE_CHAT, HANDLE_GLOBAL, GLOBAL, HANDLE_EMOTE, SEND_TO_PLAYER_ATTEMPT, AUTO_COMPLETE,
		HANDLE_LOGIN_MESSAGE, HANDLE_LOGOUT_MESSAGE, HANDLE_DEATH_MESSAGE };
	
	public final TYPE type;
	public final EntityPlayer player;
	public final String originalMessage;
	
	private String message = null;
	private boolean isHandled = false;
	private boolean isAllowed = true;
	private EntityPlayer targetPlayer = null;
	private List completions = null;
	
	public boolean isHandled() {
		return isHandled;
	}
	
	public void markHandled() {
		switch (type) {
			case AUTO_COMPLETE:
			case HANDLE_CHAT:
			case HANDLE_DEATH_MESSAGE:
			case HANDLE_EMOTE:
			case HANDLE_GLOBAL:
			case HANDLE_LOGIN_MESSAGE:
			case HANDLE_LOGOUT_MESSAGE:
				isHandled = true;
				break;
			case SEND_TO_PLAYER_ATTEMPT:
			case GLOBAL:
				break;
		}
	}
	
	public String getMessage() {
		return message == null ? originalMessage : message;
	}
	
	public boolean isMessageSet() {
		return message != null;
	}
	
	public void setMessage(String message) {
		if (canChangeMessage())
			this.message = message;
	}
	
	public EntityPlayer getTargetPlayer() {
		return targetPlayer;
	}

	public boolean isAllowed() {
		return isAllowed;
	}
	
	public void markNotAllowed() {
		isAllowed = false;
		isHandled = true;
	}
	
	public void sendAsGlobalMessage() {
		if (message != null && canChangeMessage()) {
			ChatAPI.sendChatToAllPlayers(player, message);
			MinecraftServer.getServer().getLogAgent().func_98233_a(message);
			ChatAPI.onGlobalChat(player, message);
			markHandled();
		}
	}
	
	public void addCompletion(String completion) {
		if (completions != null)
			completions.add(completion);
	}
	
	private boolean canChangeMessage() {
		switch (type) {
			case HANDLE_CHAT:
			case HANDLE_DEATH_MESSAGE:
			case HANDLE_EMOTE:
			case HANDLE_GLOBAL:
				return true;

			case HANDLE_LOGIN_MESSAGE:
			case HANDLE_LOGOUT_MESSAGE:
			case SEND_TO_PLAYER_ATTEMPT:
			case AUTO_COMPLETE:
			case GLOBAL:
				break;
		}
		
		return false;
	}

	public static PlayerChatEvent GlobalChat(EntityPlayer player, String message) {
		return new PlayerChatEvent(player, TYPE.GLOBAL, message);
	}

	public static PlayerChatEvent HandleGlobalChat(EntityPlayer player, String message) {
		return new PlayerChatEvent(player, TYPE.HANDLE_GLOBAL, message);
	}

	public static PlayerChatEvent HandleEmote(EntityPlayer player, String message) {
		return new PlayerChatEvent(player, TYPE.HANDLE_EMOTE, message);
	}

	public static PlayerChatEvent HandleChat(EntityPlayer player, String message) {
		return new PlayerChatEvent(player, TYPE.HANDLE_CHAT, message);
	}

	public static PlayerChatEvent HandleLoginMessage(EntityPlayer player) {
		return new PlayerChatEvent(player, TYPE.HANDLE_LOGIN_MESSAGE, null);
	}

	public static PlayerChatEvent HandleLogoutMessage(EntityPlayer player) {
		return new PlayerChatEvent(player, TYPE.HANDLE_LOGOUT_MESSAGE, null);
	}

	public static PlayerChatEvent HandleDeathMessage(EntityPlayer player, String message) {
		return new PlayerChatEvent(player, TYPE.HANDLE_DEATH_MESSAGE, message);
	}

	public static PlayerChatEvent SendChatToPlayerAttempt(EntityPlayer player, EntityPlayer target, String message) {
		PlayerChatEvent event = new PlayerChatEvent(player, TYPE.SEND_TO_PLAYER_ATTEMPT, message);
		event.targetPlayer = target;
		return event;
	}

	public static PlayerChatEvent HandleAutoComplete(EntityPlayer player, String text, List completions) {
		PlayerChatEvent event = new PlayerChatEvent(player, TYPE.AUTO_COMPLETE, text);
		event.completions = completions;
		return event;
	}
	
	private PlayerChatEvent(EntityPlayer player, TYPE type, String message) {
		super(player);
		this.type = type;
		this.player = player;
		this.originalMessage = message;
	}

	@Override
	public boolean isInterrupted() {
		return isHandled();
	}
}
