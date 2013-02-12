package btwmods.stats.measurements;

import btwmods.measure.TimeMeasurement;
import btwmods.stats.Type;

public class StatTick extends TimeMeasurement<Type> {
	public StatTick(Type identifier) {
		super(identifier);
	}
}
