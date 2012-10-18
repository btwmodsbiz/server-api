package btwmods;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Block;
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
import btwmods.stats.CommandStats;
import btwmods.stats.StatsEvent;
import btwmods.stats.Type;
import btwmods.stats.data.ChunkStats;
import btwmods.stats.data.EntityStats;
import btwmods.stats.data.QueuedTickStats;
import btwmods.stats.data.ServerStats;
import btwmods.stats.data.WorldStats;
import btwmods.stats.measurements.BlockUpdate;
import btwmods.stats.measurements.EntityUpdate;
import btwmods.stats.measurements.TickMeasurement;
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
	
	/**
	 * The thread that is processing stats.
	 */
	private static volatile StatsProcessor statsProcessor = null;
	
	private static EventDispatcher listeners = EventDispatcherFactory.create(new Class[] { IStatsListener.class });
	
	/**
	 * A thread-safe queue where tick stats are stored for the StatsProcessor to pick retrieve.
	 */
	private static ConcurrentLinkedQueue<QueuedTickStats> statsQueue = new ConcurrentLinkedQueue<QueuedTickStats>();
	
	/**
	 * Should only be called by ModLoader.
	 */
	public static void init() {
		server = MinecraftServer.getServer();
		((CommandHandler)server.getCommandManager()).registerCommand(new CommandStats());
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
		measurements.setEnabled(statsProcessor != null && detailedMeasurementsEnabled);
	}

	public static void endTick() {
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
	
	private static class StatsProcessor implements Runnable {
		
		@Override
		public void run() {
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
						
						// Reset the detailed stats if the detailed measurements setting has changed.
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
								worldStats[i].weather.resetCurrent();
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
							TickMeasurement measurement;
							while ((measurement = stats.measurements.poll()) != null) {
								ChunkCoordIntPair coords = null;
								
								if (measurement instanceof WorldMeasurement) {
									WorldMeasurement worldMeasurement = (WorldMeasurement)measurement;
								
									worldStats[worldMeasurement.worldIndex].measurementQueue.incrementCurrent(1);
								
									switch (measurement.identifier) {
										
										case MOB_SPAWNING:
											worldStats[worldMeasurement.worldIndex].mobSpawning.incrementCurrent(measurement.getTime());
											break;
											
										case WEATHER:
											worldStats[worldMeasurement.worldIndex].weather.incrementCurrent(measurement.getTime());
											break;
											
										case BLOCK_UPDATE:
											BlockUpdate blockUpdate = (BlockUpdate)worldMeasurement;
											worldStats[worldMeasurement.worldIndex].blockTick.incrementCurrent(measurement.getTime());
											coords = new ChunkCoordIntPair(blockUpdate.chunkX, blockUpdate.chunkZ);
											break;
											
										case ENTITIES_SECTION:
											worldStats[worldMeasurement.worldIndex].entities.incrementCurrent(measurement.getTime());
											break;
											
										case TIME_SYNC:
											worldStats[worldMeasurement.worldIndex].timeSync.incrementCurrent(measurement.getTime());
											break;
											
										case BUILD_ACTIVE_CHUNKSET:
											worldStats[worldMeasurement.worldIndex].buildActiveChunkSet.incrementCurrent(measurement.getTime());
											break;
											
										case CHECK_PLAYER_LIGHT:
											worldStats[worldMeasurement.worldIndex].checkPlayerLight.incrementCurrent(measurement.getTime());
											break;
											
										case ENTITY_UPDATE:
											EntityUpdate entityUpdate = (EntityUpdate)worldMeasurement;
											coords = new ChunkCoordIntPair(entityUpdate.chunkX, entityUpdate.chunkZ);
											
											EntityStats entityStats = worldStats[worldMeasurement.worldIndex].entityStats.get(entityUpdate.entity);
											if (entityStats == null) {
												worldStats[worldMeasurement.worldIndex].entityStats.put(entityUpdate.entity, entityStats = new EntityStats(entityUpdate.entity));
												entityStats.tickTime.record(measurement.getTime());
											}
											else {
												entityStats.tickTime.incrementCurrent(measurement.getTime());
											}
											
											entityStats.entityCount++;
											
											break;
											
										case TILE_ENTITY_UPDATE:
											TileEntityUpdate tileEntityUpdate = (TileEntityUpdate)worldMeasurement;
											coords = new ChunkCoordIntPair(tileEntityUpdate.chunkX, tileEntityUpdate.chunkZ);
											break;
									}
									
									// Get the average for this chunk and increment it.
									if (coords != null) {
										ChunkStats chunkStats = worldStats[worldMeasurement.worldIndex].chunkStats.get(coords);
										if (chunkStats == null) {
											worldStats[worldMeasurement.worldIndex].chunkStats.put(coords, chunkStats = new ChunkStats());
											chunkStats.tickTime.record(measurement.getTime());
										}
										else {
											chunkStats.tickTime.incrementCurrent(measurement.getTime());
										}
										
										if (measurement.identifier == Type.ENTITY_UPDATE)
											chunkStats.entityCount++;
									}
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
