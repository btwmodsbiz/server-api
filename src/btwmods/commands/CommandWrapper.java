package btwmods.commands;

import java.util.List;

import btwmods.IMod;
import btwmods.ModLoader;
import btwmods.TranslationsAPI;

import net.minecraft.src.CommandBase;
import net.minecraft.src.CommandException;
import net.minecraft.src.ICommandSender;

public class CommandWrapper extends CommandBase {
	
	private String registeredCommandName = null;
	public final CommandBase command;
	public final IMod mod;
	
	public CommandWrapper(CommandBase command, IMod mod) {
		if (command == null || mod == null)
			throw new NullPointerException();
		
		this.command = command;
		this.mod = mod;
	}
	
	public String getReigsteredCommandName() {
		return registeredCommandName;
	}
	
	@Override
	public String getCommandName() {
		try {
			if (registeredCommandName == null) {
				// Cache the command name the first time it's called, as this is what it will be registered as in the ICommandManager.
				return this.registeredCommandName = command.getCommandName();
			}
			else {
				return command.getCommandName();
			}
		}
		catch (RuntimeException e) {
			handleException(e, null);
			
			// Return the original command name when registering with the ICommandManager.
			return registeredCommandName;
		}
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		try {
			return command.getCommandUsage(sender);
		}
		catch (RuntimeException e) {
			handleException(e, null);
			return super.getCommandUsage(sender);
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		try {
			return command.canCommandSenderUseCommand(sender);
		}
		catch (RuntimeException e) {
			handleException(e, null);
			return false;
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		try {
			return command.addTabCompletionOptions(sender, args);
		}
		catch (RuntimeException e) {
			handleException(e, null);
			return super.addTabCompletionOptions(sender, args);
		}
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
		if (sender != null && e instanceof CommandException) {
			// Make sure the translation exists.
			if (TranslationsAPI.getTranslation(e.getMessage(), sender) == null) {
				TranslationsAPI.setTranslation(sender, e.getMessage(), command.getCommandUsage(sender));
			}
		}
		else {
			ModLoader.reportCommandFailure(e, command, mod);
		}
		
		return e;
	}
}
