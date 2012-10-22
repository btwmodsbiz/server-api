package btwmods;

import btwmods.io.Settings;

public interface IMod {
	
	/**
	 * Human-readable name of the mod that will display in the logs.
	 * @return the name of the mod.
	 * @throws Exception 
	 */
	public String getName() throws Exception;
	
	public void init(Settings settings) throws Exception;
	
	/**
	 * Cleanup and remove listeners.
	 * @throws Exception 
	 */
	public void unload() throws Exception;
}
