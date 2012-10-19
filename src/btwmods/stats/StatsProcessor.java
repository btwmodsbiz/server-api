package btwmods.stats;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ChunkCoordIntPair;
import btwmods.ModLoader;
import btwmods.StatsAPI;
import btwmods.events.EventDispatcher;
import btwmods.measure.Average;
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

public class StatsProcessor implements Runnable {
	
	public static volatile StatsProcessor statsProcessor = null;
	
	public static boolean isRunning() {
		return statsProcessor != null;
	}
	
	public static StatsProcessor setStatsProcessor(StatsProcessor statsProcessor) {
		StatsProcessor.statsProcessor = statsProcessor;
		return statsProcessor;
	}
	
	private ConcurrentLinkedQueue<QueuedTickStats> statsQueue;
	private EventDispatcher listeners;
	private boolean doingDetailedStats = StatsAPI.detailedMeasurementsEnabled;
	private int tickCounter = 0;
	private ServerStats serverStats = new ServerStats();
	private WorldStats[] worldStats = null;
	
	public StatsProcessor(EventDispatcher listeners, ConcurrentLinkedQueue<QueuedTickStats> statsQueue) {
		this.listeners = listeners;
		this.statsQueue = statsQueue;
	}
	
	@Override
	public void run() {
		try {
			
			while (statsProcessor == this) {
				
				// Stop if the thread if there are no listeners.
				if (listeners.isEmpty(IStatsListener.class)) {
					statsProcessor = null;
				}
				else {
					
					long threadStart = System.nanoTime();
					
					// Reset the detailed stats if the detailed measurements setting has changed.
					if (doingDetailedStats != StatsAPI.detailedMeasurementsEnabled || worldStats == null) {
						doingDetailedStats = StatsAPI.detailedMeasurementsEnabled;
						
						resetStats();
					}
					
					// Process all the queued tick stats.
					long polled = processQueue();
					
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
	
	private void resetStats() {
		serverStats = new ServerStats();
		
		// Initialize per-world stats.
		worldStats = new WorldStats[MinecraftServer.getServer().worldServers.length];
		for (int i = 0; i < worldStats.length; i++) {
			worldStats[i] = new WorldStats();
		}
	}
	
	/**
	 * Process all the queued tick stats.
	 * 
	 * @return the number of queued tick stats that were processed.
	 */
	private long processQueue() {
		long polled = 0;
		QueuedTickStats stats;
		while ((stats = statsQueue.poll()) != null) {
			polled++;
			tickCounter = stats.tickCounter;
			
			serverStats.lastTickEnd = Math.max(serverStats.lastTickEnd, stats.tickEnd);
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
			
			// Clean up the measurements just in case.
			stats.measurements.clear();
		}
		
		return polled;
	}
}