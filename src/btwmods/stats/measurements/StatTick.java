package btwmods.stats.measurements;

import btwmods.Stat;
import btwmods.measure.TimeMeasurement;

public class StatTick extends TimeMeasurement<Stat> {
	public StatTick(Stat identifier) {
		super(identifier);
	}
}
