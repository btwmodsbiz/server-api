package btwmods.stats.data;

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
	public final Average updatePlayerList = new Average();
	public final Average weatherEffects = new Average();
	public final Average trackedEntities = new Average();
	public final BasicStatsMap<ChunkCoordIntPair> chunkStats = new BasicStatsMap<ChunkCoordIntPair>();
	public final BasicStatsMap<Class> entityStats = new BasicStatsMap<Class>();
	public final BasicStatsMap<Class> tileEntityStats = new BasicStatsMap<Class>();
}