package btwmods.stats.data;

import btwmods.measure.Average;

public class TileEntityStats {
	public final Average tickTime;
	public final Class tileEntity;
	public int tileEntityCount;

	public TileEntityStats(Class entity) {
		tickTime = new Average();
		tileEntityCount = 0;
		this.tileEntity = entity;
	}

	public void resetCurrent() {
		tickTime.resetCurrent();
		tileEntityCount = 0;
	}
}