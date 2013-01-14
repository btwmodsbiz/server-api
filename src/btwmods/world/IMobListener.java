package btwmods.world;

import btwmods.events.IAPIListener;

public interface IMobListener extends IAPIListener {
	public void onMobAction(MobEvent event);
}
