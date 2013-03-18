package btwmods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import btwmods.chat.IPlayerAliasListener;
import btwmods.chat.IPlayerChatListener;
import btwmods.chat.PlayerAliasEvent;
import btwmods.chat.PlayerChatEvent;
import btwmods.events.EventDispatcher;
import btwmods.events.EventDispatcherFactory;
import btwmods.events.IAPIListener;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.Packet201PlayerInfo;
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
	
	private static Map<String, String> usernameToAlias = new HashMap<String, String>();
	private static Map<String, String> aliasToUsername = new HashMap<String, String>();

	public static String getUsernameAliased(String username) {
		String alias = usernameToAlias.get(username.toLowerCase());
		
		if (alias == null) {
			PlayerAliasEvent event = new PlayerAliasEvent(username);
        	((IPlayerAliasListener)listeners).onPlayerAliasAction(event);
        	
        	alias = event.alias == null ? username : event.alias;
        	setAlias(username, alias);
		}
		
		return alias;
	}
	
	public static String[] getAllUsernamesAliased() {
		String[] usernames = MinecraftServer.getServer().getAllUsernames();
		for (int i = 0; i < usernames.length; i++) {
			usernames[i] = getUsernameAliased(usernames[i]);
		}
		return usernames;
	}
	
	public static String getUsernameForAlias(String alias) {
		String username = aliasToUsername.get(alias.toLowerCase());
		return username == null ? alias : username;
	}
	
	public static void setAlias(String username, String alias) {
		System.out.println("Set alias " + username + " to " + alias);
   		String oldAlias = usernameToAlias.put(username.toLowerCase(), alias);
   		aliasToUsername.put(alias.toLowerCase(), username);
   		
   		if (oldAlias != null)
   			resetPlayerInfo(oldAlias, alias);
	}
	
	public static void removeAlias(String username) {
   		String oldAlias = usernameToAlias.remove(username.toLowerCase());
   		
   		if (oldAlias != null) {
   	   		String oldUsername = aliasToUsername.remove(oldAlias.toLowerCase());
   	   		resetPlayerInfo(oldAlias, oldUsername);
   		}
	}
	
	private static void resetPlayerInfo(String fromUsername, String toUsername) {
		MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(new Packet201PlayerInfo(fromUsername, false, 9999));
		MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(new Packet201PlayerInfo(toUsername, true, 1000));
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
