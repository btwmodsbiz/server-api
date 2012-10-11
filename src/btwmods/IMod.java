package btwmods;

public interface IMod {
	
	public void init();
	
	/**
	 * Cleanup and remove listeners.
	 */
	public void unload();
}
