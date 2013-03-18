package btwmods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import btwmods.chat.IPlayerAliasListener;
import btwmods.chat.PlayerAliasEvent;
import btwmods.events.EventDispatcher;
import btwmods.events.EventDispatcherFactory;
import btwmods.events.IAPIListener;
import btwmods.player.IPlayerChatListener;
import btwmods.player.PlayerChatEvent;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.Packet3Chat;

public class ChatAPI {
	
	private static EventDispatcher listeners = EventDispatcherFactory.create(new Class[] { IPlayerAliasListener.class, IPlayerChatListener.class });
	
	private ChatAPI() {}
	
	public static void addListener(IAPIListener listener) {
		listeners.addListener(listener);
	}

	public static void removeListener(IAPIListener listener) {
		listeners.removeListener(listener);
	}
	
	private static Map<String, String> aliasCache = new HashMap<String, String>();

	public static String getUsernameAliased(String username) {
		String alias = aliasCache.get(username.toLowerCase());
		
		if (alias == null) {
			PlayerAliasEvent event = new PlayerAliasEvent(username);
        	((IPlayerAliasListener)listeners).onPlayerAliasAction(event);
		}
		
		return alias == null ? username : alias;
	}
	
	public static String[] getAllUsernamesAliased() {
		String[] usernames = MinecraftServer.getServer().getAllUsernames();
		for (int i = 0; i < usernames.length; i++) {
			usernames[i] = getUsernameAliased(usernames[i]);
		}
		return usernames;
	}
	
	public static void sendChatToAllPlayers(EntityPlayer sender, String message) {
		sendChatToAllPlayers(sender, new Packet3Chat(message, false));
	}
	
	public static void sendChatToAllPlayers(EntityPlayer sender, Packet3Chat packet) {
		for (EntityPlayerMP player : (List<EntityPlayerMP>)MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			sendChatToPlayer(sender, player, packet);
		}
	}
	
	public static void sendChatToPlayer(EntityPlayer sender, EntityPlayerMP target, String message) {
		sendChatToPlayer(sender, target, new Packet3Chat(message, false));
	}
	
	public static void sendChatToPlayer(EntityPlayer sender, EntityPlayerMP target, Packet3Chat packet) {
		if (onSendChatToPlayerAttempt(sender, target, packet.message)) {
    		target.playerNetServerHandler.sendPacket(packet);
    	}
	}

	public static boolean onHandleChat(EntityPlayer player, String message) {
		if (!listeners.isEmpty(IPlayerChatListener.class)) {
			PlayerChatEvent event = PlayerChatEvent.HandleChat(player, message);
        	((IPlayerChatListener)listeners).onPlayerChatAction(event);
			
			if (event.isHandled())
				return true;
		}
		
		return false;
	}

	public static boolean onHandleGlobalChat(EntityPlayer player, String message) {
		if (!listeners.isEmpty(IPlayerChatListener.class)) {
			PlayerChatEvent event = PlayerChatEvent.HandleGlobalChat(player, message);
        	((IPlayerChatListener)listeners).onPlayerChatAction(event);
			
			if (event.isHandled())
				return false;
		}
		
		return true;
	}
	
	public static void onGlobalChat(EntityPlayer player, String message) {
		if (!listeners.isEmpty(IPlayerChatListener.class)) {
			PlayerChatEvent event = PlayerChatEvent.GlobalChat(player, message);
        	((IPlayerChatListener)listeners).onPlayerChatAction(event);
		}
	}

	public static boolean onHandleEmoteChat(EntityPlayer player, String message) {
		if (!listeners.isEmpty(IPlayerChatListener.class)) {
			PlayerChatEvent event = PlayerChatEvent.HandleEmote(player, message);
        	((IPlayerChatListener)listeners).onPlayerChatAction(event);
			
			if (event.isHandled())
				return false;
		}
		
		return true;
	}
	
	public static boolean onSendChatToPlayerAttempt(EntityPlayer sender, EntityPlayer target, String message) {
		PlayerChatEvent event = PlayerChatEvent.SendChatToPlayerAttempt(sender, target, message);
       	((IPlayerChatListener)listeners).onPlayerChatAction(event);
		return event.isAllowed();
	}

	public static boolean onAutoComplete(ICommandSender sender, String text, List completions) {
		if (sender instanceof EntityPlayerMP) {
			PlayerChatEvent event = PlayerChatEvent.HandleAutoComplete((EntityPlayer)sender, text, completions);
	    	((IPlayerChatListener)listeners).onPlayerChatAction(event);
			return event.isHandled();
		}
		
		return false;
	}

	

}
