package btwmods;

import btwmods.io.Settings;

public interface IMod {
	
	/**
	 * Human-readable name of the mod that will display in the logs.
	 * @return the name of the mod.
	 */
	public String getName();
	
	public void init(Settings settings);
	
	/**
	 * Cleanup and remove listeners.
	 */
	public void unload();
}
