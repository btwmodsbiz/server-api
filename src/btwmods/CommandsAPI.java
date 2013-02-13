package btwmods;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandHandler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommand;
import net.minecraft.src.ICommandSender;
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
	
	public static boolean onDoQuietCommand(ICommand command, ICommandSender sender, String[] args, boolean status) {
		if (!(sender instanceof EntityPlayer) && args.length >= 1 && args[0].equalsIgnoreCase("quiet")) {
			if (args.length == 2) {
				try {
					File statusFile = new File(args[1]);
					if (statusFile.isFile() && statusFile.length() == 0) {
						FileWriter writer = new FileWriter(statusFile);
						writer.write(status ? "OK" : "FAIL");
						writer.close();
					}
					else {
						btwmods.ModLoader.outputError("Quiet-mode status file for /" + command.getCommandName() + " was not a file or already contained data.");
					}
				}
				catch (Exception e) {
					btwmods.ModLoader.outputError(e, "Failed to write to quiet-mode status file for /" + command.getCommandName() + ": " + e.getMessage());
				}
			}
			
			return false;
		}
		
		return true;
	}
}
