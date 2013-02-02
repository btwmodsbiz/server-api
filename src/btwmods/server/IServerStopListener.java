package btwmods.server;

import btwmods.events.IAPIListener;

public interface IServerStopListener extends IAPIListener {
	void onServerStop(ServerStopEvent event);
}
