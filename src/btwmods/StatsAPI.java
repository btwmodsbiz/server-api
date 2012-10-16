package btwmods;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.CommandHandler;
import net.minecraft.src.Entity;
import net.minecraft.src.NextTickListEntry;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import btwmods.events.EventDispatcher;
import btwmods.events.EventDispatcherFactory;
import btwmods.events.IAPIListener;
import btwmods.measure.Average;
import btwmods.measure.Measurements;
import btwmods.stats.IStatsListener;
import btwmods.stats.StatsCommand;
import btwmods.stats.StatsEvent;
import btwmods.stats.Tick;
import btwmods.stats.Tick.Type;

public class StatsAPI {

	private static Measurements measurements = new Measurements<Tick>();
	
	/**
	 * Whether or not we are recording measurements this tick.
	 * This should only be modified by startTick().
	 */
	private static boolean doingMeasurements = false;
	
	/**
	 * Whether or not to skip detailed measurements or just simple ones.
	 */
	public static volatile boolean detailedMeasurementsEnabled = false;
	
	private static volatile StatsProcessor statsProcessor = null;
	
	private static EventDispatcher listeners = EventDispatcherFactory.create(new Class[] { IStatsListener.class });
	private static ConcurrentLinkedQueue<QueuedTickStats> statsQueue = new ConcurrentLinkedQueue<QueuedTickStats>();
	
	private StatsAPI() {}
	
	public static void init() {
		((CommandHandler)MinecraftServer.getServer().getCommandManager()).registerCommand(new StatsCommand());
	}

	public static void addListener(IAPIListener listener) {
		if (listener instanceof IStatsListener) {
			listeners.queuedAddListener(listener, IStatsListener.class);

			if (statsProcessor == null) {
				Thread thread = new Thread(statsProcessor = new StatsProcessor());
				thread.setName(StatsAPI.class.getSimpleName());
				thread.start();
			}
		}
		else {
			listeners.addListener(listener);
		}
	}

	public static void removeListener(IAPIListener listener) {
		if (listener instanceof IStatsListener) {
			listeners.queuedRemoveListener(listener, IStatsListener.class);
		}
		else {
			listeners.removeListener(listener);
		}
	}

	public static void startTick(MinecraftServer server, int tickCounter) {
		// Process any failures that may be queued from the last tick.
		ModLoader.processFailureQueue();
		
		// Mark if we are recording stats this tick.
		doingMeasurements = statsProcessor != null && detailedMeasurementsEnabled;
	}

	public static void endTick(MinecraftServer server, int tickCounter) {
		if (statsProcessor == null && statsQueue.size() != 0) {
			statsQueue.clear();
		}
		
		else if (statsProcessor != null) {
			QueuedTickStats stats = new QueuedTickStats();
			
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
	
	/**
	 * Begin a measurement.
	 * @param type The type of measurement to begin.
	 */
	public static void begin(Tick.Type type) {
		if (doingMeasurements)
			measurements.begin(new Tick(type));
	}
	
	/**
	 * Begin a measurement for a specific world.
	 * @param type The type of measurement to begin.
	 * @param world The world the measurement is taking place in.
	 */
	public static void begin(Tick.Type type, World world) {
		if (doingMeasurements)
			measurements.begin(new Tick(type, world));
	}

	/**
	 * Begin a measurement for a specific block tick in a world.
	 * @param type The type of measurement to begin.
	 * @param world The world the measurement is taking place in.
	 * @param entityTick The {@link NextTickListEntry} entry that is being measured.
	 */
	public static void begin(Tick.Type type, World world, NextTickListEntry entityTick) {
		if (doingMeasurements)
			measurements.begin(new Tick(type, world, entityTick));
	}

	public static void begin(Tick.Type type, World world, Entity entity) {
		if (doingMeasurements)
			measurements.begin(new Tick(type, world, entity));
	}

	public static void begin(Tick.Type type, World world, TileEntity tileEntity) {
		if (doingMeasurements)
			measurements.begin(new Tick(type, world, tileEntity));
	}
	
	/**
	 * End a measurement.
	 */
	public static void end() {
		if (doingMeasurements)
			measurements.end();
	}
	
	private static class QueuedTickStats {
		public int tickCounter;
		public long tickTime;
		public long sentPacketCount;
		public long sentPacketSize;
		public long receivedPacketCount;
		public long receivedPacketSize;
		public long[] worldTickTimes;
		ArrayDeque<Tick> measurements;
	}

	public static class StatsProcessor implements Runnable {
		
		public static class ServerStats {
			public final Average tickTime = new Average();
			public final Average sentPacketCount = new Average();
			public final Average sentPacketSize = new Average();
			public final Average receivedPacketCount = new Average();
			public final Average receivedPacketSize = new Average();
			public final Average statsThreadTime = new Average();
			public final Average statsThreadQueueCount = new Average();
		}
		
		public static class WorldStats {
			public final Average measurementQueue = new Average();
			public final Average worldTickTime = new Average();
			public final Average mobSpawning = new Average();
			public final Average blockTick = new Average();
			public final Average tickBlocksAndAmbiance = new Average();
			public final Average tickBlocksAndAmbianceSuper = new Average();
			public final Average entities = new Average();
			public final Average timeSync = new Average();
			public final Average buildActiveChunkSet = new Average();
			public final Average checkPlayerLight = new Average();
			public final Map<ChunkCoordIntPair, ChunkStats> chunkStats = new LinkedHashMap<ChunkCoordIntPair, ChunkStats>();
			public final Map<Class, EntityStats> entityStats = new LinkedHashMap<Class, EntityStats>();
		}
		
		public static class ChunkStats {
			public final Average tickTime;
			public int entityCount;

			public ChunkStats() {
				tickTime = new Average();
				entityCount = 0;
			}
			
			public void resetCurrent() {
				tickTime.resetCurrent();
				entityCount = 0;
			}
		}
		
		public static class EntityStats {
			public final Average tickTime;
			public final Class entity;
			public int entityCount;
			
			public EntityStats(Class entity) {
				tickTime = new Average();
				entityCount = 0;
				this.entity= entity; 
			}
			
			public void resetCurrent() {
				tickTime.resetCurrent();
				entityCount = 0;
			}
		}

		@Override
		public void run() {
			
			// TODO: catch Throwables and mark thread as failed and stopped.
			
			try {
			
				boolean doingDetailedStats = detailedMeasurementsEnabled;
				int tickCounter = 0;
				ServerStats serverStats = new ServerStats();
				WorldStats[] worldStats = null;
				
				while (statsProcessor == this) {
					
					// Stop if the thread if there are no listeners.
					if (listeners.isEmpty(IStatsListener.class)) {
						statsProcessor = null;
					}
					else {
						
						// Reset the detailed stats if the setting for that has changed.
						if (doingDetailedStats != detailedMeasurementsEnabled || worldStats == null) {
							doingDetailedStats = detailedMeasurementsEnabled;
							
							serverStats = new ServerStats();
							
							// Initialize per-world stats.
							worldStats = new WorldStats[MinecraftServer.getServer().worldServers.length];
							for (int i = 0; i < worldStats.length; i++) {
								worldStats[i] = new WorldStats();
							}
						}
						
						long polled = 0;
						long threadStart = System.nanoTime();
						
						// Process all the queued tick stats.
						QueuedTickStats stats;
						while ((stats = statsQueue.poll()) != null) {
							polled++;
							tickCounter = stats.tickCounter;
							
							serverStats.tickTime.record(stats.tickTime);
							serverStats.sentPacketCount.record(stats.sentPacketCount);
							serverStats.sentPacketSize.record(stats.sentPacketSize);
							serverStats.receivedPacketCount.record(stats.receivedPacketCount);
							serverStats.receivedPacketSize.record(stats.receivedPacketSize);
							
							for (int i = 0; i < worldStats.length; i++) {
								worldStats[i].worldTickTime.record(stats.worldTickTimes[i]);
	
								// Reset the measurement entries to 0.
								worldStats[i].measurementQueue.resetCurrent();
								worldStats[i].mobSpawning.resetCurrent();
								worldStats[i].blockTick.resetCurrent();
								worldStats[i].tickBlocksAndAmbiance.resetCurrent();
								worldStats[i].tickBlocksAndAmbianceSuper.resetCurrent();
								worldStats[i].entities.resetCurrent();
								worldStats[i].timeSync.resetCurrent();
								worldStats[i].buildActiveChunkSet.resetCurrent();
								worldStats[i].checkPlayerLight.resetCurrent();
								
								// Reset the ChunkStats.
								Iterator<Map.Entry<ChunkCoordIntPair, ChunkStats>> chunkStatsIterator = worldStats[i].chunkStats.entrySet().iterator();
								while (chunkStatsIterator.hasNext()) {
									Map.Entry<ChunkCoordIntPair, ChunkStats> entry = chunkStatsIterator.next();
									
									if (entry.getValue().tickTime.getAverage() == 0 && entry.getValue().tickTime.getTick() > Average.RESOLUTION * 3) {
										chunkStatsIterator.remove();
									}
									else {
										entry.getValue().resetCurrent();
									}
								}
								
								// Reset the EntityStats.
								Iterator<Map.Entry<Class, EntityStats>> entityStatsIterator = worldStats[i].entityStats.entrySet().iterator();
								while (entityStatsIterator.hasNext()) {
									Map.Entry<Class, EntityStats> entry = entityStatsIterator.next();
									
									if (entry.getValue().tickTime.getAverage() == 0 && entry.getValue().tickTime.getTick() > Average.RESOLUTION * 3) {
										entityStatsIterator.remove();
									}
									else {
										entry.getValue().resetCurrent();
									}
								}
							}
							
							// Add the time taken by each measurement type.
							Tick tick;
							while ((tick = stats.measurements.poll()) != null) {
								ChunkCoordIntPair coords = null;
								
								worldStats[tick.worldIndex].measurementQueue.incrementCurrent(1);
								
								switch (tick.identifier) {
									
									case mobSpawning:
										worldStats[tick.worldIndex].mobSpawning.incrementCurrent(tick.getTime());
										break;
										
									case tickBlocksAndAmbiance:
										worldStats[tick.worldIndex].tickBlocksAndAmbiance.incrementCurrent(tick.getTime());
										break;
										
									case tickBlocksAndAmbianceSuper:
										worldStats[tick.worldIndex].tickBlocksAndAmbianceSuper.incrementCurrent(tick.getTime());
										break;
										
									case blockTick:
										worldStats[tick.worldIndex].blockTick.incrementCurrent(tick.getTime());
										coords = new ChunkCoordIntPair(tick.blockTick.chunkX, tick.blockTick.chunkZ);
										break;
										
									case entities:
										worldStats[tick.worldIndex].entities.incrementCurrent(tick.getTime());
										break;
										
									case timeSync:
										worldStats[tick.worldIndex].timeSync.incrementCurrent(tick.getTime());
										break;
										
									case buildActiveChunkSet:
										worldStats[tick.worldIndex].buildActiveChunkSet.incrementCurrent(tick.getTime());
										break;
										
									case checkPlayerLight:
										worldStats[tick.worldIndex].checkPlayerLight.incrementCurrent(tick.getTime());
										break;
										
									case regularentity:
										coords = new ChunkCoordIntPair(tick.entityTick.chunkX, tick.entityTick.chunkZ);
										
	
										EntityStats entityStats = worldStats[tick.worldIndex].entityStats.get(tick.entityTick.entity);
										if (entityStats == null) {
											worldStats[tick.worldIndex].entityStats.put(tick.entityTick.entity, entityStats = new EntityStats(tick.entityTick.entity));
											entityStats.tickTime.record(tick.getTime());
										}
										else {
											entityStats.tickTime.incrementCurrent(tick.getTime());
										}
										
										entityStats.entityCount++;
										
										break;
										
									case tileentity:
										coords = new ChunkCoordIntPair(tick.tileEntityTick.chunkX, tick.tileEntityTick.chunkZ);
										break;
								}
								
								// Get the average for this chunk and increment it.
								if (coords != null) {
									ChunkStats chunkStats = worldStats[tick.worldIndex].chunkStats.get(coords);
									if (chunkStats == null) {
										worldStats[tick.worldIndex].chunkStats.put(coords, chunkStats = new ChunkStats());
										chunkStats.tickTime.record(tick.getTime());
									}
									else {
										chunkStats.tickTime.incrementCurrent(tick.getTime());
									}
									
									if (tick.identifier == Type.regularentity)
										chunkStats.entityCount++;
								}
							}
						}
						
						serverStats.statsThreadTime.record(System.nanoTime() - threadStart);
						serverStats.statsThreadQueueCount.record(polled);
						
						// Make sure the thread should still should be running.
						if (statsProcessor == this) {
							
							// Run all the listeners.
							StatsEvent event = new StatsEvent(MinecraftServer.getServer(), tickCounter, serverStats, worldStats);
							((IStatsListener)listeners).statsAction(event);
	
							try {
								Thread.sleep(40L);
							} catch (InterruptedException e) {
								
							}
						}
					}
				}
			}
			catch (Throwable e) {
				ModLoader.outputError(e, "StatsAPI thread failed (" + e.getClass().getSimpleName() + "): " + e.getMessage());
				
				if (statsProcessor == this)
					statsProcessor = null;
			}
		}
	}
}
