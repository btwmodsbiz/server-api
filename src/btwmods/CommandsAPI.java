package btwmods;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandHandler;
import net.minecraft.src.ICommand;
import btwmods.commands.CommandWrapper;

public class CommandsAPI {
	
	private static Map commandMap;
	private static Set commandSet;
	
	static void init() throws NoSuchFieldException, IllegalAccessException {
		Field commandMapField = CommandHandler.class.getDeclaredField("commandMap");
		commandMapField.setAccessible(true);
		commandMap = (Map)commandMapField.get(MinecraftServer.getServer().getCommandManager());
		
		Field commandSetField = CommandHandler.class.getDeclaredField("commandSet");
		commandSetField.setAccessible(true);
		commandSet = (Set)commandSetField.get(MinecraftServer.getServer().getCommandManager());
	}
	
	public static void registerCommand(ICommand command, IMod mod) {
		if (commandMap == null || commandSet == null)
			return;
		
		((CommandHandler)MinecraftServer.getServer().getCommandManager()).registerCommand(new CommandWrapper(command, mod));
	}
	
	public static void unregisterCommand(ICommand command) {
		if (commandMap == null || commandSet == null)
			return;
			
		if (command instanceof CommandWrapper)
			commandMap.remove(((CommandWrapper)command).getReigsteredCommandName());
		else
			commandMap.remove(command.getCommandName());
		
		commandSet.remove(command);
	}
}
