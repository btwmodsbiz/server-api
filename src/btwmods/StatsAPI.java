package btwmods;

import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Block;
import net.minecraft.src.ChunkProviderServer;
import net.minecraft.src.CommandHandler;
import net.minecraft.src.Entity;
import net.minecraft.src.LongHashMap;
import net.minecraft.src.NextTickListEntry;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import btwmods.events.EventDispatcher;
import btwmods.events.EventDispatcherFactory;
import btwmods.events.IAPIListener;
import btwmods.io.Settings;
import btwmods.measure.Measurements;
import btwmods.stats.IStatsListener;
import btwmods.stats.CommandStats;
import btwmods.stats.StatsProcessor;
import btwmods.stats.Type;
import btwmods.stats.data.QueuedTickStats;
import btwmods.stats.measurements.BlockUpdate;
import btwmods.stats.measurements.EntityUpdate;
import btwmods.stats.measurements.TileEntityUpdate;
import btwmods.stats.measurements.WorldMeasurement;

public class StatsAPI {
	
	private StatsAPI() {}
	
	private static boolean isInitialized = false;
	
	private static MinecraftServer server;
	
	private static volatile int tickCounter = -1;
	
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
	
	private static List[] loadedChunks;
	private static LongHashMap[] id2ChunkMap;
	private static Set[] droppedChunksSet;
	
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
		if (settings.isBoolean("[statsapi]detailedmeasurements")) {
			detailedMeasurementsEnabled = settings.getBoolean("[statsapi]detailedmeasurements");
		}

		Field loadedChunksField = ChunkProviderServer.class.getDeclaredField("loadedChunks");
		loadedChunksField.setAccessible(true);
		
		Field id2ChunkMapField = ChunkProviderServer.class.getDeclaredField("id2ChunkMap");
		id2ChunkMapField.setAccessible(true);
		
		Field droppedChunksSetField = ChunkProviderServer.class.getDeclaredField("droppedChunksSet");
		droppedChunksSetField.setAccessible(true);
		
		loadedChunks = new List[server.worldServers.length];
		id2ChunkMap = new LongHashMap[server.worldServers.length];
		droppedChunksSet = new Set[server.worldServers.length];
		
		for (int i = 0; i < server.worldServers.length; i++) {
			ChunkProviderServer provider = (ChunkProviderServer)server.worldServers[i].getChunkProvider();
			loadedChunks[i] = (List)loadedChunksField.get(provider);
			id2ChunkMap[i] = (LongHashMap)id2ChunkMapField.get(provider);
			droppedChunksSet[i] = (Set)droppedChunksSetField.get(provider);
		}
		
		((CommandHandler)server.getCommandManager()).registerCommand(new CommandStats());
		
		isInitialized = true;
	}
	
	/**
	 * Get the current tick counter.
	 * 
	 * @return The tick counter value.
	 */
	public static int getTickCounter() {
		return tickCounter;
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

	public static void startTick(int tickCounter) {
		if (!isInitialized)
			return;
		
		StatsAPI.tickCounter = tickCounter;
		
		// Process any failures that may be queued from the last tick.
		ModLoader.processFailureQueue();
		
		// Set if we are recording measurements this tick.
		measurements.setEnabled(StatsProcessor.isRunning() && detailedMeasurementsEnabled);
	}

	public static void endTick() {
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
			stats.sentPacketCount = server.sentPacketCountArray[tickCounter % 100];
			stats.sentPacketSize = server.sentPacketSizeArray[tickCounter % 100];
			stats.receivedPacketCount = server.receivedPacketCountArray[tickCounter % 100];
			stats.receivedPacketSize = server.receivedPacketSizeArray[tickCounter % 100];
			
			stats.worldTickTimes = new long[server.timeOfLastDimensionTick.length];
			stats.loadedChunks = new int[stats.worldTickTimes.length];
			stats.id2ChunkMap = new int[stats.worldTickTimes.length];
			stats.droppedChunksSet = new int[stats.worldTickTimes.length];
			for (int i = 0; i < stats.worldTickTimes.length; i++) {
				stats.worldTickTimes[i] = server.timeOfLastDimensionTick[i][tickCounter % 100];
				stats.loadedChunks[i] = loadedChunks[i].size();
				stats.id2ChunkMap[i] = id2ChunkMap[i].getNumHashElements();
				stats.droppedChunksSet[i] = droppedChunksSet[i].size();
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

	public static void begin(Type type, World world) {
		measurements.begin(new WorldMeasurement(type, world));
	}
	
	public static void beginBlockUpdate(World world, Block block, int x, int y, int z) {
		measurements.begin(new BlockUpdate(world, block, x, y, z));
	}

	public static void beginBlockUpdate(World world, NextTickListEntry blockUpdate) {
		measurements.begin(new BlockUpdate(world, blockUpdate));
	}

	public static void beginEntityUpdate(World world, Entity entity) {
		measurements.begin(new EntityUpdate(world, entity));
	}

	public static void beginTileEntityUpdate(World world, TileEntity tileEntity) {
		measurements.begin(new TileEntityUpdate(world, tileEntity));
	}
	
	/**
	 * End a measurement.
	 * @param type The type that matches the last {@link #begin} statement. 
	 */
	public static void end(Type type) {
		try {
			measurements.end();
		}
		catch (NoSuchElementException e) {
			measurements.setEnabled(false);
			detailedMeasurementsEnabled = false;
			measurements.startNew();
			ModLoader.outputError(e, "StatsAPI#end() called unexpectedly. Detailed measurements disabled.", Level.SEVERE);
		}
	}
}
