package btwmods.stats;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import btwmods.StatsAPI.StatsProcessor.EntityStats;

public class EntityStatsComparator<T> implements Comparator<Map.Entry<T, EntityStats>> {
	
	public enum Stat { TICKTIME, ENTITIES };
	public final int alternator;
	public final Stat stat;
	
	public EntityStatsComparator(Stat stat) {
		this(stat, false);
	}

	public EntityStatsComparator(Stat stat, boolean isReverse) {
		this.alternator = isReverse ? -1 : 1;
		this.stat = stat;
	}

	@Override
	public int compare(Entry<T, EntityStats> entryA, Entry<T, EntityStats> entryB) {
		switch (stat) {
			case ENTITIES:
				return (int)(entryA.getValue().entityCount - entryB.getValue().entityCount) * alternator;
			default:
				return (int)(entryA.getValue().tickTime.getTotal() - entryB.getValue().tickTime.getTotal()) * alternator;
			
		}
	}
}
