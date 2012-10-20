package btwmods.stats;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import btwmods.stats.data.TileEntityStats;

public class TileEntityStatsComparator<T> implements Comparator<Map.Entry<T, TileEntityStats>> {
	
	public enum Stat { TICKTIME, ENTITIES };
	public final int alternator;
	public final Stat stat;
	
	public TileEntityStatsComparator(Stat stat) {
		this(stat, false);
	}

	public TileEntityStatsComparator(Stat stat, boolean isReverse) {
		this.alternator = isReverse ? -1 : 1;
		this.stat = stat;
	}

	@Override
	public int compare(Entry<T, TileEntityStats> entryA, Entry<T, TileEntityStats> entryB) {
		switch (stat) {
			case ENTITIES:
				return (int)(entryA.getValue().tileEntityCount - entryB.getValue().tileEntityCount) * alternator;
			default:
				return (int)(entryA.getValue().tickTime.getTotal() - entryB.getValue().tickTime.getTotal()) * alternator;
			
		}
	}
}
