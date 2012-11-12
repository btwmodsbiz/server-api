package btwmods;

import btwmods.events.EventDispatcher;
import btwmods.events.EventDispatcherFactory;
import btwmods.events.IAPIListener;
import btwmods.io.Settings;
import btwmods.player.PlayerEventInvocationWrapper;
import btwmods.server.ITickListener;
import btwmods.server.TickEvent;

public class ServerAPI {
	private static EventDispatcher listeners = EventDispatcherFactory.create(new Class[] { ITickListener.class }, new PlayerEventInvocationWrapper());
	
	private static boolean allowUnloadSpawnChunks = true;
	
	private static boolean preloadSpawnChunks = false;
	
	private ServerAPI() {}
	
	public static void addListener(IAPIListener listener) {
		listeners.addListener(listener);
	}

	public static void removeListener(IAPIListener listener) {
		listeners.removeListener(listener);
	}
	
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