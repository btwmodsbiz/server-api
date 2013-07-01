package btwmods;

import java.util.logging.Level;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.MinecraftException;
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
	
	private static MinecraftServer server = null;
	
	public static long animalSpawnTick = 0L;
	public static boolean softcoreEnderChests = false;
	private static boolean allowUnloadSpawnChunks = false;
	private static boolean preloadSpawnChunks = true;
	private static boolean sendConnectedMessages = true;
	private static boolean shutdownOnSessionLockFailure = true;
	
	private static volatile int tickCounter = -1;
	
	private ServerAPI() {}
	
	public static void addListener(IAPIListener listener) {
		listeners.addListener(listener);
	}

	public static void removeListener(IAPIListener listener) {
		listeners.removeListener(listener);
	}
	
	static void init(Settings settings) {
		animalSpawnTick = settings.getLong("ServerAPI", "animalSpawnTick", 0L);
		allowUnloadSpawnChunks = settings.getBoolean("ServerAPI", "allowUnloadSpawnChunks", allowUnloadSpawnChunks);
		preloadSpawnChunks = settings.getBoolean("ServerAPI", "preloadSpawnChunks", preloadSpawnChunks);
		softcoreEnderChests = settings.getBoolean("ServerAPI", "softcoreEnderChests", softcoreEnderChests);
		sendConnectedMessages = settings.getBoolean("ServerAPI", "sendConnectedMessages", sendConnectedMessages);
		shutdownOnSessionLockFailure = settings.getBoolean("ServerAPI", "shutdownOnSessionLockFailure", shutdownOnSessionLockFailure);
		
		server = MinecraftServer.getServer();
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

		if (shutdownOnSessionLockFailure && tickCounter % 900 == 0) {
			try {
				server.worldServers[0].checkSessionLock();
			} catch (MinecraftException e) {
				server.initiateShutdown();
				ModLoader.outputError(e, "Failed checkSessionLock: " + e.getMessage() + ". Stopping server.", Level.SEVERE);
			}
		}
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