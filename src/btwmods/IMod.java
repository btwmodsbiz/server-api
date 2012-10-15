package btwmods;

import java.util.Map;

public interface IMod {
	
	/**
	 * Human-readable name of the mod that will display in the logs.
	 * @return the name of the mod.
	 */
	public String getName();
	
	public void init(Map<String, String> settings);
	
	/**
	 * Cleanup and remove listeners.
	 */
	public void unload();
}
