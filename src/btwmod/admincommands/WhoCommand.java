package btwmod.admincommands;

import java.util.Iterator;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;

public class WhoCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "who";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendChatToPlayer("Players:");
			
			List players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			Iterator playerIterator = players.iterator();
			while (playerIterator.hasNext()) {
				EntityPlayerMP player = (EntityPlayerMP)playerIterator.next();
				sender.sendChatToPlayer(" - " + getPlayerResult(player));
			}
		} else {
			for (int i = 0; i < args.length; i++) {
				sender.sendChatToPlayer(getPlayerResult(args[i]));
			}
		}
	}

	private static String getPlayerResult(String username) {
		EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerEntity(username);
		if (player == null)
			return "Player not found: " + username;
		else
			return getPlayerResult(player);
	}

	private static String getPlayerResult(EntityPlayerMP player) {
		return player.username + " in " + player.worldObj.provider.getDimensionName() + " at " + (int)player.posX + " " + (int)player.posY + " "
				+ (int)player.posZ;
	}

	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " [<playername> ...]";
	}

	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
		return getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
	}
}
