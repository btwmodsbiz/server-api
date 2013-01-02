package btwmods.commands;

import btwmods.Util;
import btwmods.io.Settings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.WrongUsageException;

public abstract class CommandBaseExtended extends CommandBase {
	
	public static boolean isBoolean(String[] args, int index) {
		return index < args.length && Settings.isBooleanValue(args[index]);
	}
	
	public boolean getBoolean(String[] args, int index, ICommandSender sender) {
		return getBoolean(args, index, getCommandUsage(sender));
	}
	
	public static boolean getBoolean(String[] args, int index, String usage) {
		if (!isBoolean(args, index))
			throw new WrongUsageException(usage, new Object[0]);
		
		return Settings.getBooleanValue(args[index], false);
	}
	
	public static boolean isStringMatch(String[] args, int index, String match) {
		return index < args.length && args[index].equalsIgnoreCase(match);
	}

	public static boolean isInt(String[] args, int index) {
		return index < args.length && Settings.isIntValue(args[index]);
	}

	public static boolean isFloat(String[] args, int index) {
		return index < args.length && Settings.isFloatValue(args[index]);
	}

	public static boolean isDouble(String[] args, int index) {
		return index < args.length && Settings.isDoubleValue(args[index]);
	}

	public static boolean isWorldName(String[] args, int index) {
		try {
			return index < args.length && Util.getWorldIndexFromName(args[index]) >= 0;
		}
		catch (IllegalArgumentException e) {
			return false;
		}
	}
	
	public static boolean isFullUsageAllowed(ICommandSender sender) {
		return sender instanceof EntityPlayer
				? MinecraftServer.getServer().getConfigurationManager().areCommandsAllowed(sender.getCommandSenderName())
				: true;
	}
}
