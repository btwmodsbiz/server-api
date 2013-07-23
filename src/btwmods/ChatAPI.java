package btwmods;

import java.util.Arrays;
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
import net.minecraft.src.ServerConfigurationManager;

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
        	
        	EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerEntity(username);
        	alias = event.alias == null ? (player == null ? username : player.username) : event.alias;
        	
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
		if (alias == null || username == null)
			throw new NullPointerException();
		
		ModLoader.outputInfo("Set alias " + username + " to " + alias);
   		String oldAlias = usernameToAlias.put(username.toLowerCase(), alias);
   		aliasToUsername.put(alias.toLowerCase(), username);
   		
   		if (oldAlias != null) {
   	   		EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerEntity(username);
   			resetPlayerInfo(oldAlias, alias, player == null ? 1000 : player.ping);
   		}
	}
	
	public static void removeAlias(String username, boolean updateClients) {
   		String oldAlias = usernameToAlias.remove(username.toLowerCase());
   		if (oldAlias != null) {
   	   		aliasToUsername.remove(oldAlias.toLowerCase());
   	   		
   	   		if (updateClients) {
   	   			EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerEntity(username);
   	   			resetPlayerInfo(oldAlias, getUsernameAliased(username), player == null ? 1000 : player.ping);
   	   		}
   		}
	}
	
	public static void removeAllAliases(boolean updateClients) {
		String[] usernames = usernameToAlias.keySet().toArray(new String[0]);
		for (String username : usernames)
			removeAlias(username, updateClients);
	}
	
	private static void resetPlayerInfo(String fromUsername, String toUsername, int ping) {
		MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(new Packet201PlayerInfo(fromUsername, false, 9999));
		MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(new Packet201PlayerInfo(toUsername, true, ping));
	}
	
	public static void sendChatToAllPlayers(String senderUsername, String message) {
		sendChatToAllPlayers(senderUsername, new Packet3Chat(message, false));
	}
	
	public static void sendChatToAllPlayers(String senderUsername, Packet3Chat packet) {
		for (EntityPlayerMP player : (List<EntityPlayerMP>)MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			sendChatToPlayer(senderUsername, player, packet);
		}
	}
	
	public static void sendChatToPlayer(String senderUsername, EntityPlayerMP target, String message) {
		sendChatToPlayer(senderUsername, target, new Packet3Chat(message, false));
	}
	
	public static void sendChatToPlayer(String senderUsername, EntityPlayerMP target, Packet3Chat packet) {
		if (onSendChatToPlayerAttempt(senderUsername, target.username, packet.message)) {
    		target.playerNetServerHandler.sendPacket(packet);
    	}
	}
	
	public static void sendChatToAllPlayers(String message) {
		sendChatToAllPlayers(new Packet3Chat(message, false));
	}
	
	public static void sendChatToAllPlayers(Packet3Chat packet) {
		for (EntityPlayerMP player : (List<EntityPlayerMP>)MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			player.playerNetServerHandler.sendPacket(packet);
		}
	}
	
	public static void sendChatToAllAdmins(String message) {
		sendChatToAllAdmins(new Packet3Chat(message, false));
	}
	
	public static void sendChatToAllAdmins(Packet3Chat packet) {
		ServerConfigurationManager configManager = MinecraftServer.getServer().getConfigurationManager();
		for (EntityPlayerMP player : (List<EntityPlayerMP>)MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			if (configManager.areCommandsAllowed(player.username)) {
				player.playerNetServerHandler.sendPacket(packet);
			}
		}
	}

	public static boolean onHandleChat(EntityPlayer player, String message) {
		if (!listeners.isEmpty(IPlayerChatListener.class)) {
			PlayerChatEvent event = PlayerChatEvent.HandleChat(player.username, message);
        	((IPlayerChatListener)listeners).onPlayerChatAction(event);
			
			if (event.isHandled())
				return true;
		}
		
		return false;
	}

	public static boolean onHandleGlobalChat(EntityPlayer player, String message) {
		if (!listeners.isEmpty(IPlayerChatListener.class)) {
			PlayerChatEvent event = PlayerChatEvent.HandleGlobalChat(player.username, message);
        	((IPlayerChatListener)listeners).onPlayerChatAction(event);
			
			if (event.isHandled())
				return false;
		}
		
		return true;
	}
	
	public static void onGlobalChat(String senderUsername, String message) {
		if (!listeners.isEmpty(IPlayerChatListener.class)) {
			PlayerChatEvent event = PlayerChatEvent.GlobalChat(senderUsername, message);
        	((IPlayerChatListener)listeners).onPlayerChatAction(event);
		}
	}

	public static boolean onHandleEmoteChat(EntityPlayer player, String message) {
		if (!listeners.isEmpty(IPlayerChatListener.class)) {
			PlayerChatEvent event = PlayerChatEvent.HandleEmote(player.username, message);
        	((IPlayerChatListener)listeners).onPlayerChatAction(event);
			
			if (event.isHandled())
				return false;
		}
		
		return true;
	}

	public static boolean onHandleWhisper(String[] arguments) {
		if (arguments.length < 2)
			return true;
		
		String senderUsername = arguments[0];
		String targetUsername = arguments[1];
		String[] messageArray = Arrays.copyOfRange(arguments, 2, arguments.length);
		StringBuilder message = new StringBuilder();
		for (String part : messageArray) {
			if (message.length() > 0)
				message.append(" ");
			message.append(part);
		}
		
		PlayerChatEvent event = PlayerChatEvent.HandleWhisper(senderUsername, targetUsername, message.toString());
    	((IPlayerChatListener)listeners).onPlayerChatAction(event);
		return !event.isHandled();
	}

	public static boolean onHandleLoginMessage(EntityPlayer player) {
		if (!listeners.isEmpty(IPlayerChatListener.class)) {
			PlayerChatEvent event = PlayerChatEvent.HandleLoginMessage(player.username);
        	((IPlayerChatListener)listeners).onPlayerChatAction(event);
			
			if (event.isHandled())
				return false;
		}
		
		return true;
	}

	public static boolean onHandleLogoutMessage(EntityPlayer player) {
		if (!listeners.isEmpty(IPlayerChatListener.class)) {
			PlayerChatEvent event = PlayerChatEvent.HandleLogoutMessage(player.username);
        	((IPlayerChatListener)listeners).onPlayerChatAction(event);
			
			if (event.isHandled())
				return false;
		}
		
		return true;
	}

	public static boolean onHandleDeathMessage(EntityPlayer player, String deathMessage) {
		if (!listeners.isEmpty(IPlayerChatListener.class)) {
			PlayerChatEvent event = PlayerChatEvent.HandleDeathMessage(player.username, deathMessage.replace(player.getTranslatedEntityName(), player.username));
        	((IPlayerChatListener)listeners).onPlayerChatAction(event);
			
			if (event.isHandled())
				return false;
		}
		
		return true;
	}
	
	public static boolean onSendChatToPlayerAttempt(String senderUsername, String targetUsername, String message) {
		PlayerChatEvent event = PlayerChatEvent.SendChatToPlayerAttempt(senderUsername, targetUsername, message);
       	((IPlayerChatListener)listeners).onPlayerChatAction(event);
		return event.isAllowed();
	}

	public static boolean onAutoComplete(ICommandSender sender, String text, List completions) {
		if (sender instanceof EntityPlayerMP) {
			PlayerChatEvent event = PlayerChatEvent.HandleAutoComplete(((EntityPlayer)sender).username, text, completions);
	    	((IPlayerChatListener)listeners).onPlayerChatAction(event);
			return event.isHandled();
		}
		
		return false;
	}

	public static boolean onDeferredAutoComplete(ICommandSender sender, String text) {
		if (sender instanceof EntityPlayerMP) {
			PlayerChatEvent event = PlayerChatEvent.HandleDeferredAutoComplete(((EntityPlayer)sender).username, text);
	    	((IPlayerChatListener)listeners).onPlayerChatAction(event);
			return event.isHandled();
		}
		
		return false;
	}
}
