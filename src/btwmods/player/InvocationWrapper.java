package btwmods.player;

import java.lang.reflect.InvocationTargetException;

import btwmods.events.EventDispatcherFactory.Invocation;
import btwmods.events.IInvocationWrapper;

public class InvocationWrapper implements IInvocationWrapper {

	@Override
	public void handleInvocation(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
		if (invocation.args.length == 1 && invocation.args[0] instanceof InstanceEvent) {
			InstanceEvent event = (InstanceEvent)invocation.args[0];
			if (event.getType() == InstanceEvent.TYPE.READ_NBT || event.getType() == InstanceEvent.TYPE.WRITE_NBT) {
				event.setModCompound(invocation.listener.getMod());
				invocation.invoke();
				event.unsetModCompound();
				return;
			}
		}
		
		invocation.invoke();
	}

}
