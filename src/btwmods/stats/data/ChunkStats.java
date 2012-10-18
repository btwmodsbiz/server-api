package btwmods.stats.data;

import btwmods.measure.Average;

public class ChunkStats {
	public final Average tickTime;
	public int entityCount;

	public ChunkStats() {
		tickTime = new Average();
		entityCount = 0;
	}
	
	public void resetCurrent() {
		tickTime.resetCurrent();
		entityCount = 0;
	}
}