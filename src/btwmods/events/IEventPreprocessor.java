package btwmods.events;

import java.util.EventObject;

public interface IEventPreprocessor {
	public EventObject processEvent(EventObject event);
}
