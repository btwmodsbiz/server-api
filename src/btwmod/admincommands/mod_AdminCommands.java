package btwmod.admincommands;

import btwmods.CommandsAPI;
import btwmods.IMod;
import btwmods.io.Settings;

public class mod_AdminCommands implements IMod {
	
	private WhoCommand whoCommand;
	private DumpTrackedCommand dumpTrackedCommand;

	@Override
	public String getName() throws Exception {
		return "Admin Commands";
	}

	@Override
	public void init(Settings settings) throws Exception {
		CommandsAPI.registerCommand(whoCommand = new WhoCommand(), this);
		CommandsAPI.registerCommand(dumpTrackedCommand = new DumpTrackedCommand(), this);
	}

	@Override
	public void unload() throws Exception {
		CommandsAPI.unregisterCommand(whoCommand);
		CommandsAPI.unregisterCommand(dumpTrackedCommand);
	}
}
