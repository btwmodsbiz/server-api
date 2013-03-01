package btwmods.stats;

import java.util.List;
import java.util.Set;

import btwmods.StatsAPI;
import btwmods.Util;
import btwmods.commands.CommandBaseExtended;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.WrongUsageException;

public class CommandStats extends CommandBaseExtended {

	@Override
	public String getCommandName() {
		return "stats";
	}
	
	public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName() + " [<profile>]";
    }

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendChatToPlayer(Util.COLOR_YELLOW + "Stats profile: " + StatsAPI.statProfile);
		}
		else if (args.length == 1) {
			Set<String> profileNames = StatsAPI.getProfileNames();
			if (profileNames.contains(args[0].toLowerCase())) {
				StatsAPI.statProfile = args[0].toLowerCase();
				sender.sendChatToPlayer(Util.COLOR_YELLOW + "Stats profile set to " + args[0].toLowerCase() + ".");
			}
			else {
				sender.sendChatToPlayer(Util.COLOR_RED + "A stats profile with that name does not exist.");
			}
		}
		else {
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1)
			return getListOfStringsFromIterableMatchingLastWord(args, StatsAPI.getProfileNames());
		
		return super.addTabCompletionOptions(sender, args);
	}
	
	
}
