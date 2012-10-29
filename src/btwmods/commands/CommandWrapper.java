package btwmods.commands;

import java.util.List;

import btwmods.IMod;
import btwmods.ModLoader;

import net.minecraft.src.CommandBase;
import net.minecraft.src.CommandException;
import net.minecraft.src.ICommand;
import net.minecraft.src.ICommandSender;

public class CommandWrapper extends CommandBase {
	
	private String registeredCommandName = null;
	private List registeredCommandAliases = null;
	public final ICommand command;
	public final IMod mod;
	
	public CommandWrapper(ICommand command, IMod mod) throws Exception {
		if (command == null || mod == null)
			throw new NullPointerException();
		
		this.command = command;
		this.mod = mod;
		
		// Cache the command name and aliases, since it is important that they do not change.
		registeredCommandName = command.getCommandName();
		registeredCommandAliases = command.getCommandAliases();
	}
	
	public String getReigsteredCommandName() {
		return registeredCommandName;
	}
	
	@Override
	public String getCommandName() {
		return registeredCommandName;
	}

	@Override
	public List getCommandAliases() {
		return registeredCommandAliases;
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
		if (sender == null || !(e instanceof CommandException)) {
			ModLoader.reportCommandFailure(e, registeredCommandName, command, mod);
		}
		
		return e;
	}
}
