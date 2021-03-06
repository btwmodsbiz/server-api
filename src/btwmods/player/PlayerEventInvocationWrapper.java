package btwmods.player;

import java.lang.reflect.InvocationTargetException;

import btwmods.events.EventDispatcherFactory.Invocation;
import btwmods.events.IInvocationWrapper;

public class PlayerEventInvocationWrapper implements IInvocationWrapper {

	@Override
	public void handleInvocation(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
		if (invocation.args.length == 1 && invocation.args[0] instanceof PlayerInstanceEvent) {
			PlayerInstanceEvent event = (PlayerInstanceEvent)invocation.args[0];
			if (event.getType() == PlayerInstanceEvent.TYPE.READ_NBT || event.getType() == PlayerInstanceEvent.TYPE.WRITE_NBT) {
				event.setModCompound(invocation.listener.getMod());
				invocation.invoke();
				event.unsetModCompound();
				return;
			}
		}
		
		invocation.invoke();
	}

}
