package btwmods;

import java.util.Properties;

public interface IMod<T> {

	public void setDefaultProperties(Properties properties);
	
	public void init(T parent);
	
	/**
	 * Cleanup and remove listeners.
	 */
	public void unload();
}
