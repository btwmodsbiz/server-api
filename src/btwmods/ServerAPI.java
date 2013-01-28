package btwmods;

import java.util.Random;

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
	private static int chanceForWildWolf = 0;
	
	private static volatile int tickCounter = -1;
	
	private static Random rand = new Random();
	
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
		chanceForWildWolf = settings.getInt("ServerAPI", "chanceForWildWolf", chanceForWildWolf);
		softcoreEnderChests = settings.getBoolean("ServerAPI", "softcoreEnderChests", softcoreEnderChests);
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

	public static boolean onIsBabyWolfWild() {
		return chanceForWildWolf > 0 && rand.nextInt(chanceForWildWolf) == 0;
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