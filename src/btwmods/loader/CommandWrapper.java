package btwmods.loader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import btwmods.IMod;
import btwmods.ModLoader;
import btwmods.TranslationsAPI;
import btwmods.events.IAPIListener;

import net.minecraft.src.CommandBase;
import net.minecraft.src.CommandException;
import net.minecraft.src.ICommandSender;

public class CommandWrapper extends CommandBase {
	
	private CommandBase command;
	private boolean addedTranslation = false;
	private IMod mod;
	
	public CommandWrapper(CommandBase command, IMod mod) {
		this.command = command;
		this.mod = mod;
	}
	
	@Override
	public String getCommandName() {
		return command.getCommandName();
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return command.getCommandUsage(sender);
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		try {
			return command.canCommandSenderUseCommand(sender);
		}
		catch (RuntimeException e) {
			throw handleException(e, sender);
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		return command.addTabCompletionOptions(sender, args);
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		try {
			command.processCommand(sender, args);
		}
		catch (RuntimeException e) {
			throw handleException(e, sender);
		}
	}
	
	private RuntimeException handleException(RuntimeException e, ICommandSender sender) {
		if (e instanceof CommandException) {
			// Make sure the translation exists.
			if (TranslationsAPI.getTranslation(e.getMessage(), sender) == null) {
				TranslationsAPI.setTranslation(sender, e.getMessage(), command.getCommandUsage(sender));
			}
		}
		else {
			ModLoader.reportModFailure(e, mod);
		}
		
		return e;
	}
}
