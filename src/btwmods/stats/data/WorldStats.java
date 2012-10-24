package btwmods.stats.data;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.src.ChunkCoordIntPair;
import btwmods.measure.Average;

public class WorldStats {
	public final Average measurementQueue = new Average();
	public final Average worldTickTime = new Average();
	public final Average mobSpawning = new Average();
	public final Average blockTick = new Average();
	public final Average weather = new Average();
	public final Average entities = new Average();
	public final Average timeSync = new Average();
	public final Average buildActiveChunkSet = new Average();
	public final Average checkPlayerLight = new Average();
	public final Average loadedChunks = new Average(); // TODO: make this longer?
	public int id2ChunkMap = -1;
	public final Average droppedChunksSet = new Average();
	public final Average entitiesRegular = new Average();
	public final Average entitiesRemove = new Average();
	public final Average entitiesTile = new Average();
	public final Average entitiesTilePending = new Average();
	public final Average lightingAndRain = new Average();
	public final Average updatePlayerEntities = new Average();
	public final Average updateTrackedEntityPlayerLists = new Average();
	public final Average weatherEffects = new Average();
	public final Average trackedEntities = new Average();
	public final BasicStatsMap<ChunkCoordIntPair> chunkStats = new BasicStatsMap<ChunkCoordIntPair>();
	public final BasicStatsMap<String> entityStats = new BasicStatsMap<String>();
	public final BasicStatsMap<Class> tileEntityStats = new BasicStatsMap<Class>();
	public final BasicStatsMap<String> trackedEntityStats = new BasicStatsMap<String>();
	
	// Reset the averages to 0.
	public void reset() {
		measurementQueue.resetCurrent();
		mobSpawning.resetCurrent();
		blockTick.resetCurrent();
		weather.resetCurrent();
		entities.resetCurrent();
		timeSync.resetCurrent();
		buildActiveChunkSet.resetCurrent();
		checkPlayerLight.resetCurrent();
		entitiesRegular.resetCurrent();
		entitiesRemove.resetCurrent();
		entitiesTile.resetCurrent();
		entitiesTilePending.resetCurrent();
		lightingAndRain.resetCurrent();
		updatePlayerEntities.resetCurrent();
		updateTrackedEntityPlayerLists.resetCurrent();
		weatherEffects.resetCurrent();

		resetCurrentMap(chunkStats);
		resetCurrentMap(entityStats);
		resetCurrentMap(tileEntityStats);
		resetCurrentMap(trackedEntityStats);
	}
	
	private static void resetCurrentMap(BasicStatsMap map) {
		Iterator<Map.Entry<Class, BasicStats>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Class, BasicStats> entry = iterator.next();
			
			if (entry.getValue().tickTime.getAverage() == 0.0D && entry.getValue().tickTime.getTick() > Average.RESOLUTION) {
				iterator.remove();
			}
			else {
				entry.getValue().resetCurrent();
			}
		}
	}
}