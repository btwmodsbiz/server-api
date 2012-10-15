package btwmods.events;

import java.lang.reflect.InvocationTargetException;
import btwmods.events.EventDispatcherFactory.Invocation;

public interface IInvocationWrapper {
	public void handleInvocation(Invocation invocation) throws InvocationTargetException, IllegalAccessException;
}
