package btwmods.stats;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ChunkCoordIntPair;
import btwmods.ModLoader;
import btwmods.StatsAPI;
import btwmods.events.EventDispatcher;
import btwmods.measure.Measurement;
import btwmods.network.NetworkType;
import btwmods.stats.data.BasicStats;
import btwmods.stats.data.QueuedTickStats;
import btwmods.stats.data.ServerStats;
import btwmods.stats.data.WorldStats;
import btwmods.stats.measurements.StatUpdateBlock;
import btwmods.stats.measurements.StatUpdateEntity;
import btwmods.stats.measurements.StatNetwork;
import btwmods.stats.measurements.StatNetworkPlayer;
import btwmods.stats.measurements.StatSpawnedLiving;
import btwmods.stats.measurements.StatUpdateTileEntity;
import btwmods.stats.measurements.StatUpdateEntityTracked;
import btwmods.stats.measurements.StatWorld;

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
						((IStatsListener)listeners).onStats(event);

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
			
			serverStats.handlerInovcations = stats.handlerInvocations;
			
			for (int i = 0; i < worldStats.length; i++) {
				worldStats[i].worldTickTime.record(stats.worldTickTimes[i]);
				worldStats[i].loadedChunks.record(stats.loadedChunks[i]);
				worldStats[i].id2ChunkMap.record(stats.id2ChunkMap[i]);
				worldStats[i].droppedChunksSet.record(stats.droppedChunksSet[i]);
				worldStats[i].loadedEntityList.record(stats.loadedEntityList[i]);
				worldStats[i].loadedTileEntityList.record(stats.loadedTileEntityList[i]);
				worldStats[i].trackedEntities.record(stats.trackedEntities[i]);
				
				// Reset the current value of averages to 0.
				worldStats[i].reset();
			}
			
			// Add the time taken by each measurement type.
			Measurement measurement;
			while ((measurement = stats.measurements.poll()) != null) {
				ChunkCoordIntPair coords = null;
				
				if (measurement instanceof StatNetwork) {
					StatNetwork statNetwork = (StatNetwork)measurement;
					
					if (statNetwork.identifier == NetworkType.RECEIVED)
						serverStats.bytesReceivedFromPlayers += (long)statNetwork.size;
					else
						serverStats.bytesSentToPlayers += (long)statNetwork.size;
					
					if (measurement instanceof StatNetworkPlayer) {
						// TODO: record player network usage.
					}
				}
				
				else if (measurement instanceof StatWorld) {
					StatWorld statWorld = (StatWorld)measurement;
				
					worldStats[statWorld.worldIndex].measurementQueue.incrementCurrent(1);
				
					switch (statWorld.identifier) {
						
						case MOB_SPAWNING:
							worldStats[statWorld.worldIndex].mobSpawning.incrementCurrent(statWorld.getTime());
							break;
							
						case MOOD_LIGHT_AND_WEATHER:
							worldStats[statWorld.worldIndex].weather.incrementCurrent(statWorld.getTime());
							break;
							
						case BLOCK_UPDATE:
							StatUpdateBlock statUpdateBlock = (StatUpdateBlock)statWorld;
							worldStats[statWorld.worldIndex].blockTick.incrementCurrent(statWorld.getTime());
							coords = new ChunkCoordIntPair(statUpdateBlock.chunkX, statUpdateBlock.chunkZ);
							break;
							
						case ENTITIES_SECTION:
							worldStats[statWorld.worldIndex].entities.incrementCurrent(statWorld.getTime());
							break;
							
						case TIME_SYNC:
							worldStats[statWorld.worldIndex].timeSync.incrementCurrent(statWorld.getTime());
							break;
							
						case BUILD_ACTIVE_CHUNKSET:
							worldStats[statWorld.worldIndex].buildActiveChunkSet.incrementCurrent(statWorld.getTime());
							break;
							
						case CHECK_PLAYER_LIGHT:
							worldStats[statWorld.worldIndex].checkPlayerLight.incrementCurrent(statWorld.getTime());
							break;
							
						case ENTITY_UPDATE:
							StatUpdateEntity statUpdateEntity = (StatUpdateEntity)statWorld;
							coords = new ChunkCoordIntPair(statUpdateEntity.chunkX, statUpdateEntity.chunkZ);
							BasicStats entityStats = worldStats[statWorld.worldIndex].entityStats.get(statUpdateEntity.name);
							if (entityStats == null) {
								worldStats[statWorld.worldIndex].entityStats.put(statUpdateEntity.name, entityStats = new BasicStats());
								entityStats.tickTime.record(statWorld.getTime());
							}
							else {
								entityStats.tickTime.incrementCurrent(statWorld.getTime());
							}
							
							entityStats.count++;
							
							break;
							
						case TILE_ENTITY_UPDATE:
							StatUpdateTileEntity statUpdateTileEntity = (StatUpdateTileEntity)statWorld;
							coords = new ChunkCoordIntPair(statUpdateTileEntity.chunkX, statUpdateTileEntity.chunkZ);
							
							BasicStats tileEntityStats = worldStats[statWorld.worldIndex].tileEntityStats.get(statUpdateTileEntity.tileEntity);
							if (tileEntityStats == null) {
								worldStats[statWorld.worldIndex].tileEntityStats.put(statUpdateTileEntity.tileEntity, tileEntityStats = new BasicStats());
								tileEntityStats.tickTime.record(statWorld.getTime());
							}
							else {
								tileEntityStats.tickTime.incrementCurrent(statWorld.getTime());
							}
							
							tileEntityStats.count++;
							
							break;
							
						case ENTITIES_REGULAR:
							worldStats[statWorld.worldIndex].entitiesRegular.incrementCurrent(statWorld.getTime());
							break;
							
						case ENTITIES_REMOVE:
							worldStats[statWorld.worldIndex].entitiesRemove.incrementCurrent(statWorld.getTime());
							break;
							
						case ENTITIES_TILE:
							worldStats[statWorld.worldIndex].entitiesTile.incrementCurrent(statWorld.getTime());
							break;
							
						case ENTITIES_TILEPENDING:
							worldStats[statWorld.worldIndex].entitiesTilePending.incrementCurrent(statWorld.getTime());
							break;
							
						case LIGHTNING_AND_RAIN:
							worldStats[statWorld.worldIndex].lightingAndRain.incrementCurrent(statWorld.getTime());
							break;
							
						case UPDATE_PLAYER_ENTITIES:
							worldStats[statWorld.worldIndex].updatePlayerEntities.incrementCurrent(statWorld.getTime());
							break;
							
						case UPDATE_TRACKED_ENTITY_PLAYER_LISTS:
							worldStats[statWorld.worldIndex].updateTrackedEntityPlayerLists.incrementCurrent(statWorld.getTime());
							break;
							
						case WEATHER_EFFECTS:
							worldStats[statWorld.worldIndex].weatherEffects.incrementCurrent(statWorld.getTime());
							break;
							
						case UPDATE_TRACKED_ENTITY_PLAYER_LIST:
							StatUpdateEntityTracked statUpdateEntityTracked = (StatUpdateEntityTracked)statWorld;
							BasicStats trackedEntityStats = worldStats[statWorld.worldIndex].trackedEntityStats.get(statUpdateEntityTracked.name);
							if (trackedEntityStats == null) {
								worldStats[statWorld.worldIndex].trackedEntityStats.put(statUpdateEntityTracked.name, trackedEntityStats = new BasicStats());
								trackedEntityStats.tickTime.record(statWorld.getTime());
							}
							else {
								trackedEntityStats.tickTime.incrementCurrent(statWorld.getTime());
							}
							
							trackedEntityStats.count++;
							break;
							
						case LOAD_CHUNK:
							worldStats[statWorld.worldIndex].chunkLoading.incrementCurrent(1L);
							worldStats[statWorld.worldIndex].chunkLoadingTime.incrementCurrent(statWorld.getTime());
							break;
							
						case SPAWN_LIVING:
							StatSpawnedLiving statSpawnedLiving = (StatSpawnedLiving)statWorld;
							worldStats[statWorld.worldIndex].spawnedLiving.incrementCurrent(statSpawnedLiving.count);
							break;
					}
					
					// Get the average for this chunk and increment it.
					if (coords != null) {
						BasicStats chunkStats = worldStats[statWorld.worldIndex].chunkStats.get(coords);
						if (chunkStats == null) {
							worldStats[statWorld.worldIndex].chunkStats.put(coords, chunkStats = new BasicStats());
							chunkStats.tickTime.record(statWorld.getTime());
						}
						else {
							chunkStats.tickTime.incrementCurrent(statWorld.getTime());
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