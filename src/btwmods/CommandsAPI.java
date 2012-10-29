package btwmods;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandHandler;
import net.minecraft.src.ICommand;
import btwmods.commands.CommandWrapper;

public class CommandsAPI {
	
	private static Map<ICommand, CommandWrapper> commandWrapperLookup = new HashMap<ICommand, CommandWrapper>();
	
	public static void registerCommand(ICommand command, IMod mod) {
		CommandWrapper wrapper = null;
		try {
			wrapper = new CommandWrapper(command, mod);
			commandWrapperLookup.put(command, wrapper);
			((CommandHandler)MinecraftServer.getServer().getCommandManager()).registerCommand(wrapper);
		}
		catch (Exception e) {
			ModLoader.reportCommandRegistrationFailure(e, command, mod);
		}
	}
	
	public static void unregisterCommand(ICommand command) {
		CommandWrapper wrapper = commandWrapperLookup.get(command);
		if (wrapper != null)
			((CommandHandler)MinecraftServer.getServer().getCommandManager()).unregisterCommand(wrapper);
	}
}
