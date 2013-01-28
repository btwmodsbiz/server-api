package btwmods.player;

import btwmods.PlayerAPI;
import btwmods.events.APIEvent;
import btwmods.events.IEventInterrupter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Packet3Chat;

public class PlayerChatEvent extends APIEvent implements IEventInterrupter {
	
	public enum TYPE { HANDLE_CHAT, HANDLE_GLOBAL, GLOBAL };
	
	public final TYPE type;
	public final EntityPlayer player;
	public final String originalMessage;
	
	private String message = null;
	private boolean isHandled = false;
	
	public boolean isHandled() {
		return isHandled;
	}
	
	public void markHandled() {
		if (type == TYPE.HANDLE_GLOBAL)
			isHandled = true;
	}
	
	public String getMessage() {
		return message == null ? originalMessage : message;
	}
	
	public boolean isMessageSet() {
		return message != null;
	}
	
	public void setMessage(String message) {
		if (type == TYPE.HANDLE_GLOBAL)
			this.message = message;
	}
	
	public void sendAsGlobalMessage() {
		if (type == TYPE.HANDLE_GLOBAL && message != null) {
			MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(new Packet3Chat(message, false));
			MinecraftServer.getServer().logger.info(message);
			PlayerAPI.onGlobalChat(player, message);
			markHandled();
		}
	}

	public static PlayerChatEvent GlobalChat(EntityPlayer player, String message) {
		PlayerChatEvent event = new PlayerChatEvent(player, TYPE.GLOBAL, message);
		return event;
	}

	public static PlayerChatEvent HandleGlobalChat(EntityPlayer player, String message) {
		PlayerChatEvent event = new PlayerChatEvent(player, TYPE.HANDLE_GLOBAL, message);
		return event;
	}

	public static PlayerChatEvent HandleChat(EntityPlayer player, String message) {
		PlayerChatEvent event = new PlayerChatEvent(player, TYPE.HANDLE_CHAT, message);
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
