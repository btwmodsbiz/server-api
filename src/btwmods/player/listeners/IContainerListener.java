package btwmods.player.listeners;

import btwmods.IAPIListener;
import btwmods.player.events.ContainerEvent;

public interface IContainerListener extends IAPIListener {
	public void containerAction(ContainerEvent event);
	//TODO: public void containerContentsEjected(Container container, int x, int y, int z);
}