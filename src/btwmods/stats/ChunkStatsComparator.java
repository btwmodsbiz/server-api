package btwmods.stats;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import btwmods.stats.data.ChunkStats;

public class ChunkStatsComparator<T> implements Comparator<Map.Entry<T, ChunkStats>> {
	
	public enum Stat { TICKTIME, ENTITIES };
	public final int alternator;
	public final Stat stat;
	
	public ChunkStatsComparator(Stat stat) {
		this(stat, false);
	}

	public ChunkStatsComparator(Stat stat, boolean isReverse) {
		this.alternator = isReverse ? -1 : 1;
		this.stat = stat;
	}

	@Override
	public int compare(Entry<T, ChunkStats> entryA, Entry<T, ChunkStats> entryB) {
		switch (stat) {
			case ENTITIES:
				return (int)(entryA.getValue().entityCount - entryB.getValue().entityCount) * alternator;
			default:
				return (int)(entryA.getValue().tickTime.getTotal() - entryB.getValue().tickTime.getTotal()) * alternator;
			
		}
	}
}
