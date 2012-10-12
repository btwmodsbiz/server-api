package btwmods.player.listeners;

import java.util.EventListener;
import btwmods.player.events.InstanceEvent;

public interface IInstanceListener extends EventListener {
	void instanceAction(InstanceEvent event);
}
