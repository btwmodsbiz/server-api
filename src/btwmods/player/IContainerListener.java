package btwmods.player;

import btwmods.IAPIListener;

public interface IContainerListener extends IAPIListener {
	public void containerAction(ContainerEvent event);
	//TODO: public void containerContentsEjected(Container container, int x, int y, int z);
}
