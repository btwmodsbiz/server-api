package btwmods.stats.measurements;

import btwmods.Stat;
import btwmods.measure.Measurement;

public class StatValue extends Measurement<Stat> {

	public final long value;
	
	public StatValue(Stat identifier, long value) {
		super(identifier);
		this.value = value;
	}
}
