package btwmods;

import btwmods.events.EventDispatcher;
import btwmods.events.EventDispatcherFactory;
import btwmods.events.IAPIListener;
import btwmods.io.Settings;
import btwmods.server.ITickListener;
import btwmods.server.TickEvent;

public class ServerAPI {
	private static EventDispatcher listeners = EventDispatcherFactory.create(new Class[] { ITickListener.class });
	
	private static boolean allowUnloadSpawnChunks = false;
	
	private static boolean preloadSpawnChunks = true;
	
	private ServerAPI() {}
	
	public static void addListener(IAPIListener listener) {
		listeners.addListener(listener);
	}

	public static void removeListener(IAPIListener listener) {
		listeners.removeListener(listener);
	}
	
	static void init(Settings settings) {
		if (settings.isBoolean("ServerAPI", "allowUnloadSpawnChunks")) {
			allowUnloadSpawnChunks = settings.getBoolean("ServerAPI", "allowUnloadSpawnChunks");
		}
		
		if (settings.isBoolean("ServerAPI", "preloadSpawnChunks")) {
			preloadSpawnChunks = settings.getBoolean("ServerAPI", "preloadSpawnChunks");
		}
	}

	public static boolean doInitialChunkLoad() {
		return preloadSpawnChunks;
	}

	public static boolean doUnloadSpawnChunks() {
		return allowUnloadSpawnChunks;
	}
	
	public static void onStartTick(int tickCounter) {
		StatsAPI.onStartTick(tickCounter);
		
		if (!listeners.isEmpty(ITickListener.class)) {
        	TickEvent event = TickEvent.StartTick(tickCounter);
        	((ITickListener)listeners).onTick(event);
		}
	}

	public static void onEndTick(int tickCounter) {
		if (!listeners.isEmpty(ITickListener.class)) {
        	TickEvent event = TickEvent.EndTick(tickCounter);
        	((ITickListener)listeners).onTick(event);
		}
		
		StatsAPI.onEndTick();
	}
}