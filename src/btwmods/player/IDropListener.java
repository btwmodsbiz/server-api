package btwmods.player;

import btwmods.events.IAPIListener;

public interface IDropListener extends IAPIListener {
	void onPlayerItemDrop(DropEvent event);
}
