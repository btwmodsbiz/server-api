package btwmods.events;

import java.util.EventListener;

import btwmods.IMod;

public interface IAPIListener extends EventListener {
	public IMod getMod();
}
