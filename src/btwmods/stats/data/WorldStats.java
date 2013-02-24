package btwmods.stats.data;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.src.ChunkCoordIntPair;
import btwmods.Stat;
import btwmods.measure.Average;
import btwmods.measure.Measurement;

public class WorldStats {
	public final Average measurementQueue = new Average();
	public final EnumMap<Stat, Average> averages;
	public final Map<ChunkCoordIntPair, Average> timeByChunk = new LinkedHashMap<ChunkCoordIntPair, Average>();
	public final EnumMap<Stat, Map<Class, Average>> timeByClass = new EnumMap<Stat, Map<Class, Average>>(Stat.class);
	public final List<Measurement> measurements = new ArrayList<Measurement>();
	
	public WorldStats() {
		averages = new EnumMap<Stat, Average>(Stat.class);
		for (Stat stat : Stat.values()) {
			averages.put(stat, new Average(stat.averageResolution));
			timeByClass.put(stat, new LinkedHashMap<Class, Average>());
		}
	}
	
	// Reset the averages to 0.
	public void resetCurrent() {
		measurementQueue.resetCurrent();
		measurements.clear();
		
		for (Entry<Stat, Average> entry : averages.entrySet()) {
			entry.getValue().resetCurrent();
		}
		
		for (Entry<ChunkCoordIntPair, Average> entry : timeByChunk.entrySet()) {
			entry.getValue().resetCurrent();
		}
		
		for (Entry<Stat, Map<Class, Average>> entry : timeByClass.entrySet()) {
			for (Entry<Class, Average> subEntry : entry.getValue().entrySet()) {
				subEntry.getValue().resetCurrent();
			}
		}
	}
}