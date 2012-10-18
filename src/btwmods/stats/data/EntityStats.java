package btwmods.stats.data;

import btwmods.measure.Average;

public class EntityStats {
	public final Average tickTime;
	public final Class entity;
	public int entityCount;
	
	public EntityStats(Class entity) {
		tickTime = new Average();
		entityCount = 0;
		this.entity= entity; 
	}
	
	public void resetCurrent() {
		tickTime.resetCurrent();
		entityCount = 0;
	}
}