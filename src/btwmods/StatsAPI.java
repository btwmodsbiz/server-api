package btwmods;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Block;
import net.minecraft.src.CommandHandler;
import net.minecraft.src.Entity;
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
	
	private static MinecraftServer server;
	
	private static volatile int tickCounter = -1;
	
	/**
	 * The detailed measurements that have been take this tick. 
	 */
	private static Measurements measurements = new Measurements();
	
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
	 */
	public static void init(Settings settings) {
		server = MinecraftServer.getServer();
		((CommandHandler)server.getCommandManager()).registerCommand(new CommandStats());

		// Load settings
		if (settings.isBoolean("detailedmeasurements")) {
			detailedMeasurementsEnabled = settings.getBoolean("detailedmeasurements");
		}
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
		StatsAPI.tickCounter = tickCounter;
		
		// Process any failures that may be queued from the last tick.
		ModLoader.processFailureQueue();
		
		// Set if we are recording measurements this tick.
		measurements.setEnabled(StatsProcessor.isRunning() && detailedMeasurementsEnabled);
	}

	public static void endTick() {
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
			for (int i = 0; i < stats.worldTickTimes.length; i++) {
				stats.worldTickTimes[i] = server.timeOfLastDimensionTick[i][tickCounter % 100];
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
	 */
	public static void end() {
		measurements.end();
	}
}
