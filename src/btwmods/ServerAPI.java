package btwmods;

import btwmods.io.Settings;

public class ServerAPI {
	
	private static boolean allowUnloadSpawnChunks = true;
	
	private static boolean preloadSpawnChunks = false;
	
	private ServerAPI() {}
	
	static void init(Settings settings) {
		if (settings.isBoolean("[serverapi]allowunloadspawnchunks")) {
			allowUnloadSpawnChunks = settings.getBoolean("[serverapi]allowunloadspawnchunks");
		}
		
		if (settings.isBoolean("[serverapi]preloadspawnchunks")) {
			preloadSpawnChunks = settings.getBoolean("[serverapi]preloadspawnchunks");
		}
	}

	public static boolean doInitialChunkLoad() {
		return preloadSpawnChunks;
	}

	public static boolean doUnloadSpawnChunks() {
		return allowUnloadSpawnChunks;
	}
}
