package btwmods.player;

import btwmods.events.IAPIListener;

public interface IContainerListener extends IAPIListener {
	public void containerAction(ContainerEvent event);
	//TODO: public void containerContentsEjected(Container container, int x, int y, int z);
}
