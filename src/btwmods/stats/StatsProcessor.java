package btwmods.stats;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ChunkCoordIntPair;
import btwmods.ModLoader;
import btwmods.StatsAPI;
import btwmods.events.EventDispatcher;
import btwmods.measure.Measurement;
import btwmods.stats.data.BasicStats;
import btwmods.stats.data.QueuedTickStats;
import btwmods.stats.data.ServerStats;
import btwmods.stats.data.WorldStats;
import btwmods.stats.measurements.BlockUpdate;
import btwmods.stats.measurements.EntityUpdate;
import btwmods.stats.measurements.NetworkMeasurement;
import btwmods.stats.measurements.PlayerNetworkMeasurement;
import btwmods.stats.measurements.TileEntityUpdate;
import btwmods.stats.measurements.TrackedEntityUpdate;
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
			serverStats.players = stats.players;
			serverStats.sentPacketCount.record(stats.sentPacketCount);
			serverStats.sentPacketSize.record(stats.sentPacketSize);
			serverStats.receivedPacketCount.record(stats.receivedPacketCount);
			serverStats.receivedPacketSize.record(stats.receivedPacketSize);
			
			serverStats.bytesSent = stats.bytesSent;
			serverStats.bytesReceived = stats.bytesReceived;
			
			for (int i = 0; i < worldStats.length; i++) {
				worldStats[i].worldTickTime.record(stats.worldTickTimes[i]);
				worldStats[i].loadedChunks.record(stats.loadedChunks[i]);
				worldStats[i].id2ChunkMap.record(stats.id2ChunkMap[i]);
				worldStats[i].droppedChunksSet.record(stats.droppedChunksSet[i]);
				worldStats[i].trackedEntities.record(stats.trackedEntities[i]);
				
				// Reset the current value of averages to 0.
				worldStats[i].reset();
			}
			
			// Add the time taken by each measurement type.
			Measurement measurement;
			while ((measurement = stats.measurements.poll()) != null) {
				ChunkCoordIntPair coords = null;
				
				if (measurement instanceof NetworkMeasurement) {
					NetworkMeasurement networkMeasurement = (NetworkMeasurement)measurement;
					
					if (networkMeasurement.identifier == NetworkType.RECEIVED)
						serverStats.bytesReceivedFromPlayers += (long)networkMeasurement.size;
					else
						serverStats.bytesSentToPlayers += (long)networkMeasurement.size;
					
					if (measurement instanceof PlayerNetworkMeasurement) {
						// TODO: record player network usage.
					}
				}
				
				else if (measurement instanceof WorldMeasurement) {
					WorldMeasurement worldMeasurement = (WorldMeasurement)measurement;
				
					worldStats[worldMeasurement.worldIndex].measurementQueue.incrementCurrent(1);
				
					switch (worldMeasurement.identifier) {
						
						case MOB_SPAWNING:
							worldStats[worldMeasurement.worldIndex].mobSpawning.incrementCurrent(worldMeasurement.getTime());
							break;
							
						case MOOD_LIGHT_AND_WEATHER:
							worldStats[worldMeasurement.worldIndex].weather.incrementCurrent(worldMeasurement.getTime());
							break;
							
						case BLOCK_UPDATE:
							BlockUpdate blockUpdate = (BlockUpdate)worldMeasurement;
							worldStats[worldMeasurement.worldIndex].blockTick.incrementCurrent(worldMeasurement.getTime());
							coords = new ChunkCoordIntPair(blockUpdate.chunkX, blockUpdate.chunkZ);
							break;
							
						case ENTITIES_SECTION:
							worldStats[worldMeasurement.worldIndex].entities.incrementCurrent(worldMeasurement.getTime());
							break;
							
						case TIME_SYNC:
							worldStats[worldMeasurement.worldIndex].timeSync.incrementCurrent(worldMeasurement.getTime());
							break;
							
						case BUILD_ACTIVE_CHUNKSET:
							worldStats[worldMeasurement.worldIndex].buildActiveChunkSet.incrementCurrent(worldMeasurement.getTime());
							break;
							
						case CHECK_PLAYER_LIGHT:
							worldStats[worldMeasurement.worldIndex].checkPlayerLight.incrementCurrent(worldMeasurement.getTime());
							break;
							
						case ENTITY_UPDATE:
							EntityUpdate entityUpdate = (EntityUpdate)worldMeasurement;
							coords = new ChunkCoordIntPair(entityUpdate.chunkX, entityUpdate.chunkZ);
							BasicStats entityStats = worldStats[worldMeasurement.worldIndex].entityStats.get(entityUpdate.name);
							if (entityStats == null) {
								worldStats[worldMeasurement.worldIndex].entityStats.put(entityUpdate.name, entityStats = new BasicStats());
								entityStats.tickTime.record(worldMeasurement.getTime());
							}
							else {
								entityStats.tickTime.incrementCurrent(worldMeasurement.getTime());
							}
							
							entityStats.count++;
							
							break;
							
						case TILE_ENTITY_UPDATE:
							TileEntityUpdate tileEntityUpdate = (TileEntityUpdate)worldMeasurement;
							coords = new ChunkCoordIntPair(tileEntityUpdate.chunkX, tileEntityUpdate.chunkZ);
							
							BasicStats tileEntityStats = worldStats[worldMeasurement.worldIndex].tileEntityStats.get(tileEntityUpdate.tileEntity);
							if (tileEntityStats == null) {
								worldStats[worldMeasurement.worldIndex].tileEntityStats.put(tileEntityUpdate.tileEntity, tileEntityStats = new BasicStats());
								tileEntityStats.tickTime.record(worldMeasurement.getTime());
							}
							else {
								tileEntityStats.tickTime.incrementCurrent(worldMeasurement.getTime());
							}
							
							tileEntityStats.count++;
							
							break;
							
						case ENTITIES_REGULAR:
							worldStats[worldMeasurement.worldIndex].entitiesRegular.incrementCurrent(worldMeasurement.getTime());
							break;
							
						case ENTITIES_REMOVE:
							worldStats[worldMeasurement.worldIndex].entitiesRemove.incrementCurrent(worldMeasurement.getTime());
							break;
							
						case ENTITIES_TILE:
							worldStats[worldMeasurement.worldIndex].entitiesTile.incrementCurrent(worldMeasurement.getTime());
							break;
							
						case ENTITIES_TILEPENDING:
							worldStats[worldMeasurement.worldIndex].entitiesTilePending.incrementCurrent(worldMeasurement.getTime());
							break;
							
						case LIGHTNING_AND_RAIN:
							worldStats[worldMeasurement.worldIndex].lightingAndRain.incrementCurrent(worldMeasurement.getTime());
							break;
							
						case UPDATE_PLAYER_ENTITIES:
							worldStats[worldMeasurement.worldIndex].updatePlayerEntities.incrementCurrent(worldMeasurement.getTime());
							break;
							
						case UPDATE_TRACKED_ENTITY_PLAYER_LISTS:
							worldStats[worldMeasurement.worldIndex].updateTrackedEntityPlayerLists.incrementCurrent(worldMeasurement.getTime());
							break;
							
						case WEATHER_EFFECTS:
							worldStats[worldMeasurement.worldIndex].weatherEffects.incrementCurrent(worldMeasurement.getTime());
							break;
							
						case UPDATE_TRACKED_ENTITY_PLAYER_LIST:
							TrackedEntityUpdate trackedEntityUpdate = (TrackedEntityUpdate)worldMeasurement;
							BasicStats trackedEntityStats = worldStats[worldMeasurement.worldIndex].trackedEntityStats.get(trackedEntityUpdate.name);
							if (trackedEntityStats == null) {
								worldStats[worldMeasurement.worldIndex].trackedEntityStats.put(trackedEntityUpdate.name, trackedEntityStats = new BasicStats());
								trackedEntityStats.tickTime.record(worldMeasurement.getTime());
							}
							else {
								trackedEntityStats.tickTime.incrementCurrent(worldMeasurement.getTime());
							}
							
							trackedEntityStats.count++;
							break;
					}
					
					// Get the average for this chunk and increment it.
					if (coords != null) {
						BasicStats chunkStats = worldStats[worldMeasurement.worldIndex].chunkStats.get(coords);
						if (chunkStats == null) {
							worldStats[worldMeasurement.worldIndex].chunkStats.put(coords, chunkStats = new BasicStats());
							chunkStats.tickTime.record(worldMeasurement.getTime());
						}
						else {
							chunkStats.tickTime.incrementCurrent(worldMeasurement.getTime());
						}
						
						if (measurement.identifier == Type.ENTITY_UPDATE)
							chunkStats.count++;
					}
				}
			}
			
			// Clean up the measurements just in case.
			stats.measurements.clear();
		}
		
		return polled;
	}
}