package btwmods.player;

import btwmods.events.APIEvent;
import btwmods.events.IEventInterrupter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.Packet3Chat;

public class PlayerChatEvent extends APIEvent implements IEventInterrupter {
	
	public enum TYPE { GLOBAL };
	
	public final TYPE type;
	public final NetServerHandler handler;
	public final EntityPlayer player;
	public final String originalMessage;
	
	private String message = null;
	private boolean isHandled = false;
	
	public boolean isHandled() {
		return message != null && isHandled;
	}
	
	public void markHandled() {
		isHandled = true;
	}
	
	public String getMessage() {
		return message == null ? originalMessage : message;
	}
	
	public boolean isMessageSet() {
		return message != null;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void sendAsGlobalMessage() {
		if (message != null) {
			MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(new Packet3Chat(getMessage(), false));
			MinecraftServer.getServer().logger.info(getMessage());
			markHandled();
		}
	}

	public static PlayerChatEvent GlobalChat(NetServerHandler handler, EntityPlayer player, String message) {
		PlayerChatEvent event = new PlayerChatEvent(handler, player, TYPE.GLOBAL, message);
		return event;
	}
	
	private PlayerChatEvent(NetServerHandler handler, EntityPlayer player, TYPE type, String message) {
		super(player);
		this.type = type;
		this.handler = handler;
		this.player = player;
		this.originalMessage = message;
	}

	@Override
	public boolean isInterrupted() {
		return isHandled();
	}
}
