package btwmod.admincommands;

import btwmods.CommandsAPI;
import btwmods.IMod;
import btwmods.io.Settings;

public class mod_AdminCommands implements IMod {
	
	private WhoCommand whoCommand;

	@Override
	public String getName() throws Exception {
		return "Admin Commands";
	}

	@Override
	public void init(Settings settings) throws Exception {
		CommandsAPI.registerCommand(whoCommand = new WhoCommand(), this);
	}

	@Override
	public void unload() throws Exception {
		CommandsAPI.unregisterCommand(whoCommand);
	}
}
