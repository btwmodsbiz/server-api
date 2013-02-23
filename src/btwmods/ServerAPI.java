package btwmods;

import btwmods.events.EventDispatcher;
import btwmods.events.EventDispatcherFactory;
import btwmods.events.IAPIListener;
import btwmods.io.Settings;
import btwmods.server.IServerStopListener;
import btwmods.server.ITickListener;
import btwmods.server.ServerStopEvent;
import btwmods.server.TickEvent;

public class ServerAPI {
	private static EventDispatcher listeners = EventDispatcherFactory.create(new Class[] { ITickListener.class, IServerStopListener.class });
	
	public static boolean softcoreEnderChests = false;
	private static boolean allowUnloadSpawnChunks = false;
	private static boolean preloadSpawnChunks = true;
	private static boolean sendConnectedMessages = true;
	
	private static volatile int tickCounter = -1;
	
	private ServerAPI() {}
	
	public static void addListener(IAPIListener listener) {
		listeners.addListener(listener);
	}

	public static void removeListener(IAPIListener listener) {
		listeners.removeListener(listener);
	}
	
	static void init(Settings settings) {
		allowUnloadSpawnChunks = settings.getBoolean("ServerAPI", "allowUnloadSpawnChunks", allowUnloadSpawnChunks);
		preloadSpawnChunks = settings.getBoolean("ServerAPI", "preloadSpawnChunks", preloadSpawnChunks);
		softcoreEnderChests = settings.getBoolean("ServerAPI", "softcoreEnderChests", softcoreEnderChests);
		sendConnectedMessages = settings.getBoolean("ServerAPI", "sendConnectedMessages", sendConnectedMessages);
	}
	
	/**
	 * Get the current tick counter.
	 * 
	 * @return The tick counter value.
	 */
	public static int getTickCounter() {
		return tickCounter;
	}

	public static boolean doInitialChunkLoad() {
		return preloadSpawnChunks;
	}

	public static boolean doUnloadSpawnChunks() {
		return allowUnloadSpawnChunks;
	}
	
	public static boolean doConnectedMessages() {
		return sendConnectedMessages;
	}
	
	public static void onStartTick(int tickCounter) {
		ServerAPI.tickCounter = tickCounter;
		StatsAPI.onStartTick();
		
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

	public static void onStopServerPre() {
    	ServerStopEvent event = ServerStopEvent.Pre();
    	((IServerStopListener)listeners).onServerStop(event);
	}

	public static void onStopServerPost() {
    	ServerStopEvent event = ServerStopEvent.Post();
    	((IServerStopListener)listeners).onServerStop(event);
	}
}