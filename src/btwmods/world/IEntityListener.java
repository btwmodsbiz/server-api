package btwmods.world;

import btwmods.events.IAPIListener;

public interface IEntityListener extends IAPIListener {
	public void onEntityAction(EntityEvent event);
}
