package btwmods.stats.data;

import btwmods.measure.Average;

public class BasicStats {
	public final Average tickTime = new Average();
	public int count = 0;

	public void resetCurrent() {
		tickTime.resetCurrent();
		count = 0;
	}
}
