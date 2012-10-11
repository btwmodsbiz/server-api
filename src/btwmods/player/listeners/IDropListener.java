package btwmods.player.listeners;

import java.util.EventListener;

import btwmods.player.events.DropEvent;

public interface IDropListener extends EventListener {
	void dropAction(DropEvent event);
}
