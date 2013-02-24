package btwmods;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandHandler;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.World;
import btwmods.events.EventDispatcher;
import btwmods.events.EventDispatcherFactory;
import btwmods.events.IAPIListener;
import btwmods.io.Settings;
import btwmods.measure.Measurement;
import btwmods.measure.Measurements;
import btwmods.measure.TimeMeasurement;
import btwmods.network.NetworkType;
import btwmods.stats.IStatsListener;
import btwmods.stats.CommandStats;
import btwmods.stats.StatsProcessor;
import btwmods.stats.data.QueuedTickStats;
import btwmods.stats.measurements.StatNetworkPlayer;
import btwmods.stats.measurements.StatSpawnedLiving;

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
	
	private static long bytesSent = 0;
	private static long bytesReceived = 0;
	
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
			
			stats.bytesReceived = bytesReceived;
			stats.bytesSent = bytesSent;
			
			stats.handlerInvocations = EventDispatcherFactory.getInvocationCount();
			
			stats.worldTickTimes = new long[server.timeOfLastDimensionTick.length];
			stats.loadedChunks = new int[stats.worldTickTimes.length];
			stats.id2ChunkMap = new int[stats.worldTickTimes.length];
			stats.droppedChunksSet = new int[stats.worldTickTimes.length];
			stats.loadedEntityList = new int[stats.worldTickTimes.length];
			stats.loadedTileEntityList = new int[stats.worldTickTimes.length];
			stats.trackedEntities = new int[stats.worldTickTimes.length];
			for (int i = 0; i < stats.worldTickTimes.length; i++) {
				stats.worldTickTimes[i] = server.timeOfLastDimensionTick[i][tickCounter % 100];
				stats.loadedChunks[i] = WorldAPI.getLoadedChunks()[i].size();
				stats.id2ChunkMap[i] = WorldAPI.getCachedChunks()[i].getNumHashElements();
				stats.droppedChunksSet[i] = WorldAPI.getDroppedChunks()[i].size();
				stats.loadedEntityList[i] = server.worldServers[i].loadedEntityList.size();
				stats.loadedTileEntityList[i] = server.worldServers[i].loadedTileEntityList.size();
				stats.trackedEntities[i] = WorldAPI.getTrackedEntities()[i].size();
			}
			
			if (!measurements.completedMeasurements()) {
				measurements.setEnabled(false);
				detailedMeasurementsEnabled = false;
				measurements.startNew();
				ModLoader.outputError("StatsAPI detected that not all measurements were completed properly. Detailed measurements disabled.", Level.SEVERE);
			}
			
			// Save measurements and clear it for the next round.
			stats.measurements = measurements.startNew();
			
			statsQueue.add(stats);
		}
	}

	public static void record(Measurement measurement) {
		measurements.record(measurement);
	}
	
	public static void onRecordNetworkIO(NetworkType type, int bytes) {
		recordNetworkIO(type, bytes, null);
	}
	
	public static void recordNetworkIO(NetworkType type, int bytes, EntityPlayerMP player) {
		if (type == NetworkType.RECEIVED)
			bytesReceived += (long)bytes;
		else
			bytesSent += (long)bytes;
		
		if (player != null)
			record(new StatNetworkPlayer(type, player, bytes));
	}

	public static void recordSpawning(World world, int spawned) {
		measurements.record(new StatSpawnedLiving(world, spawned));
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
