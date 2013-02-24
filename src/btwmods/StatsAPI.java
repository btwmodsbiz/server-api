package btwmods;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandHandler;
import btwmods.events.EventDispatcher;
import btwmods.events.EventDispatcherFactory;
import btwmods.events.IAPIListener;
import btwmods.io.Settings;
import btwmods.measure.Measurement;
import btwmods.measure.Measurements;
import btwmods.measure.TimeMeasurement;
import btwmods.stats.IStatsListener;
import btwmods.stats.CommandStats;
import btwmods.stats.StatsProcessor;
import btwmods.stats.data.QueuedTickStats;
import btwmods.stats.measurements.StatWorld;
import btwmods.stats.measurements.StatWorldValue;

public class StatsAPI {
	
	private StatsAPI() {}
	
	private static boolean isInitialized = false;
	
	private static MinecraftServer server;
	
	/**
	 * The detailed measurements that have been take this tick. 
	 */
	private static Measurements measurements = new Measurements(false);
	
	/**
	 * Whether or not to skip detailed measurements, starting with the next tick.
	 */
	public static volatile boolean detailedMeasurementsEnabled = false;
	
	private static EventDispatcher listeners = EventDispatcherFactory.create(new Class[] { IStatsListener.class });
	
	/**
	 * A thread-safe queue where tick stats are stored for the StatsProcessor to pick retrieve.
	 */
	private static ConcurrentLinkedQueue<QueuedTickStats> statsQueue = new ConcurrentLinkedQueue<QueuedTickStats>();
	
	/**
	 * Should only be called by ModLoader.
	 * 
	 * @param settings 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 */
	static void init(Settings settings) throws NoSuchFieldException, IllegalAccessException {
		server = MinecraftServer.getServer();

		// Load settings
		detailedMeasurementsEnabled = settings.getBoolean("StatsAPI", "detailedMeasurements", detailedMeasurementsEnabled);
		
		((CommandHandler)server.getCommandManager()).registerCommand(new CommandStats());
		
		isInitialized = true;
	}

	/**
	 * Add a listener supported by this API.
	 * 
	 * @param listener The listener to add.
	 */
	public static void addListener(IAPIListener listener) {
		if (listener instanceof IStatsListener) {
			listeners.queuedAddListener(listener, IStatsListener.class);

			if (!StatsProcessor.isRunning()) {
				Thread thread = new Thread(StatsProcessor.setStatsProcessor(new StatsProcessor(listeners, statsQueue)));
				thread.setName(StatsAPI.class.getSimpleName());
				thread.start();
			}
		}
		else {
			listeners.addListener(listener);
		}
	}

	/**
	 * Remove a listener that has been added to this API.
	 * 
	 * @param listener The listener to remove.
	 */
	public static void removeListener(IAPIListener listener) {
		if (listener instanceof IStatsListener) {
			listeners.queuedRemoveListener(listener, IStatsListener.class);
		}
		else {
			listeners.removeListener(listener);
		}
	}

	public static void onStartTick() {
		if (!isInitialized)
			return;
		
		// Process any failures that may be queued from the last tick.
		ModLoader.processFailureQueue();
		
		// Set if we are recording measurements this tick.
		measurements.setEnabled(StatsProcessor.isRunning() && detailedMeasurementsEnabled);
	}

	public static void onEndTick() {
		int tickCounter = ServerAPI.getTickCounter();
		
		if (!isInitialized)
			return;
		
		if (!StatsProcessor.isRunning()) {
			statsQueue.clear();
		}
		
		else {
			QueuedTickStats stats = new QueuedTickStats();
			
			stats.tickEnd = System.currentTimeMillis();
			stats.tickCounter = tickCounter;
			stats.tickTime = server.tickTimeArray[tickCounter % 100];
			stats.players = server.getConfigurationManager().getAllUsernames();
			stats.sentPacketCount = server.sentPacketCountArray[tickCounter % 100];
			stats.sentPacketSize = server.sentPacketSizeArray[tickCounter % 100];
			stats.receivedPacketCount = server.receivedPacketCountArray[tickCounter % 100];
			stats.receivedPacketSize = server.receivedPacketSizeArray[tickCounter % 100];
			
			stats.bytesReceived = Stat.bytesReceived;
			stats.bytesSent = Stat.bytesSent;
			
			stats.handlerInvocations = EventDispatcherFactory.getInvocationCount();
			
			if (!measurements.completedMeasurements()) {
				measurements.setEnabled(false);
				detailedMeasurementsEnabled = false;
				measurements.startNew();
				ModLoader.outputError("StatsAPI detected that not all measurements were completed properly. Detailed measurements disabled.", Level.SEVERE);
			}
			
			for (int i = 0, l = server.worldServers.length; i < l; i++) {
				measurements.record(new StatWorld(Stat.WORLD_TICK, i).record(server.timeOfLastDimensionTick[i][tickCounter % 100]));
				measurements.record(new StatWorldValue(Stat.WORLD_LOADED_CHUNKS, i, WorldAPI.getLoadedChunks()[i].size()));
				measurements.record(new StatWorldValue(Stat.WORLD_CACHED_CHUNKS, i, WorldAPI.getCachedChunks()[i].getNumHashElements()));
				measurements.record(new StatWorldValue(Stat.WORLD_DROPPED_CHUNKS, i, WorldAPI.getDroppedChunks()[i].size()));
				measurements.record(new StatWorldValue(Stat.WORLD_LOADED_ENTITIES, i, server.worldServers[i].loadedEntityList.size()));
				measurements.record(new StatWorldValue(Stat.WORLD_LOADED_TILE_ENTITIES, i, server.worldServers[i].loadedTileEntityList.size()));
				measurements.record(new StatWorldValue(Stat.WORLD_TRACKED_ENTITIES, i, WorldAPI.getTrackedEntities()[i].size()));
			}
			
			// Save measurements and clear it for the next round.
			stats.measurements = measurements.startNew();
			
			statsQueue.add(stats);
		}
	}

	public static void record(Measurement measurement) {
		measurements.record(measurement);
	}
	
	public static void begin(TimeMeasurement<Stat> measurement) {
		measurements.begin(measurement);
	}
	
	/**
	 * End a measurement.
	 * @param stat The stat that matches the last {@link #begin}. 
	 */
	public static void end(Stat stat) {
		try {
			measurements.end(stat);
		}
		catch (IllegalStateException e) {
			measurements.setEnabled(false);
			detailedMeasurementsEnabled = false;
			measurements.startNew();
			ModLoader.outputError(e, "StatsAPI#end() call did not match the type at the top of the stack. Detailed measurements disabled: " + e.getMessage(), Level.SEVERE);
		}
		catch (NoSuchElementException e) {
			measurements.setEnabled(false);
			detailedMeasurementsEnabled = false;
			measurements.startNew();
			ModLoader.outputError(e, "StatsAPI#end() called unexpectedly. Detailed measurements disabled.", Level.SEVERE);
		}
	}
}
