package btwmods.stats.data;

import java.util.LinkedHashMap;
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
	public final Map<ChunkCoordIntPair, ChunkStats> chunkStats = new LinkedHashMap<ChunkCoordIntPair, ChunkStats>();
	public final Map<Class, EntityStats> entityStats = new LinkedHashMap<Class, EntityStats>();
}