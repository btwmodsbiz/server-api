package btwmods.entity;

import btwmods.events.IAPIListener;

public interface ISpawnLivingListener extends IAPIListener {
	public void onSpawnLivingAction(SpawnLivingEvent event);
}
