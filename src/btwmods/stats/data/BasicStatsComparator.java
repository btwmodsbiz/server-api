package btwmods.stats.data;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;


public class BasicStatsComparator<T> implements Comparator<Map.Entry<T, BasicStats>> {
	
	public enum Stat { TICKTIME, COUNT };
	public final int alternator;
	public final Stat stat;
	
	public BasicStatsComparator(Stat stat) {
		this(stat, false);
	}

	public BasicStatsComparator(Stat stat, boolean isReverse) {
		this.alternator = isReverse ? -1 : 1;
		this.stat = stat;
	}

	@Override
	public int compare(Entry<T, BasicStats> entryA, Entry<T, BasicStats> entryB) {
		switch (stat) {
			case COUNT:
				return (int)(entryA.getValue().count - entryB.getValue().count) * alternator;
			default:
				return (int)(entryA.getValue().tickTime.getTotal() - entryB.getValue().tickTime.getTotal()) * alternator;
			
		}
	}
}
