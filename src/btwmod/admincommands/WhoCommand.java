package btwmod.admincommands;

import java.util.Iterator;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;

public class WhoCommand extends CommandBase {
	
	private final mod_AdminCommands mod;
	
	public WhoCommand(mod_AdminCommands mod) {
		this.mod = mod;
	}

	@Override
	public String getCommandName() {
		return "who";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length == 0) {
			List players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			
			if (players.size() == 0) {
				sender.sendChatToPlayer("No players online.");
			}
			else {
				Iterator playerIterator = players.iterator();
				while (playerIterator.hasNext()) {
					EntityPlayerMP player = (EntityPlayerMP)playerIterator.next();
					sender.sendChatToPlayer(getPlayerResult(player));
				}
			}
		} else {
			for (int i = 0; i < args.length; i++) {
				sender.sendChatToPlayer(getPlayerResult(args[i]));
			}
		}
	}

	private String getPlayerResult(String username) {
		EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerEntity(username);
		if (player == null)
			return "Player not found: " + username;
		else
			return getPlayerResult(player);
	}

	private String getPlayerResult(EntityPlayerMP player) {
		long seconds = mod.getTimeSinceLastPlayerAction(player);
		return player.username + " in " + player.worldObj.provider.getDimensionName() + " at " + (long)player.posX + " " + (long)player.posY + " "
				+ (long)player.posZ + (seconds >= mod.getSecondsForAFK() ? " (AFK " + seconds + " seconds)" : "");
	}

	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " [<playername> ...]";
	}

	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
		return getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
	}
}
