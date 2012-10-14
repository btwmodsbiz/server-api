package btwmods.player;

import btwmods.IAPIListener;

public interface IDropListener extends IAPIListener {
	void dropAction(DropEvent event);
}
