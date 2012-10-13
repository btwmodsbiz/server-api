package btwmods;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventDispatcher implements InvocationHandler {
	
	/**
	 * Create a new event dispatcher that supports the supplied IAPIListener classes.
	 * 
	 * @param listenerClasses the classes this dispatcher will support.
	 * @return a proxy that implements all the supplied IAPIListener.
	 */
	public static IAPIListener create(Class<IAPIListener>[] listenerClasses) {
		if (listenerClasses == null || listenerClasses.length == 0)
			throw new IllegalArgumentException("listenerClasses cannot be null or an empty array");
		
		EventDispatcher dispatcher = new EventDispatcher(listenerClasses);
		return dispatcher.setProxy((IAPIListener)Proxy.newProxyInstance(EventDispatcher.class.getClassLoader(), listenerClasses, dispatcher));
	}
	
	private Map<Class, Set<IAPIListener>> lookup = new HashMap<Class, Set<IAPIListener>>();
	private Class[] listenerClasses;
	private IAPIListener proxy;
	
	/**
	 * Initializes this dispatcher's lookup.
	 * @param listenerClasses
	 */
	private EventDispatcher(Class[] listenerClasses) {
		this.listenerClasses = listenerClasses;
		init(listenerClasses);
	}
	
	private IAPIListener setProxy(IAPIListener proxy) {
		return (this.proxy = proxy);
	}
	
	/**
	 * Creates the lookup entries for the listener classes.
	 */
	private void init(Class[] listenerClasses) {
		for (int i = 0; i < listenerClasses.length; i++) {
			if ((lookup.get(listenerClasses[i])) == null) {
				lookup.put(listenerClasses[i], new HashSet<IAPIListener>());
			}
		}
	}
	
	private Set<IAPIListener> getListeners(Class listenerClass) {
		return lookup.get(listenerClass);
	}
	
	/**
	 * Add the listener to all the matching IAPIListeners supported by this dispatcher.
	 * 
	 * @return true if the listener was added successfully; false if the listener did not exist or is not not supported by this dispatcher.
	 */
	public void addListener(IAPIListener listener) {
		Class listenerClass = listener.getClass();
		for (int i = 0; i < listenerClasses.length; i++) {
			if (listenerClasses[i].isAssignableFrom(listenerClass)) {
				addListenerInternal(listener, listenerClasses[i]);
			}
		}
	}

	/**
	 * Adds the listener for only the specified listener class.
	 * 
	 * @return true if the listener was added successfully; false if the listener did not exist or is not not supported by this dispatcher.
	 */
	public boolean addListener(IAPIListener listener, Class<IAPIListener> listenerClass) {
		if (!listenerClass.isAssignableFrom(listener.getClass()))
			throw new IllegalArgumentException("listener is not an instance of listenerClass");

		return listenerClass.isAssignableFrom(proxy.getClass()) && addListenerInternal(listener, listenerClass);
	}

	private boolean addListenerInternal(IAPIListener listener, Class<IAPIListener> listenerClass) {
		return getListeners(listenerClass).add(listener);
	}

	/**
	 * Removes the listener to all the matching IAPIListeners supported by this dispatcher.
	 * 
	 * @return true if the listener was removed successfully; false if the listener did not exist or is not not supported by this dispatcher.
	 */
	public void removeListener(IAPIListener listener) {
		Class listenerClass = listener.getClass();
		for (int i = 0; i < listenerClasses.length; i++) {
			if (listenerClasses[i].isAssignableFrom(listenerClass)) {
				removeListenerInternal(listener, listenerClasses[i]);
			}
		}
	}

	/**
	 * Removes the listener from only the specified listener class.
	 * 
	 * @return true if the listener was added successfully; false if the listener did not exist or is not not supported by this dispatcher.
	 */
	public boolean removeListener(Class<IAPIListener> listenerClass, IAPIListener listener) {
		if (!listenerClass.isAssignableFrom(listener.getClass()))
			throw new IllegalArgumentException("listener is not an instance of listenerClass");

		return listenerClass.isAssignableFrom(proxy.getClass()) && removeListenerInternal(listener, listenerClass);
	}

	private boolean removeListenerInternal(IAPIListener listener, Class<IAPIListener> listenerClass) {
		return getListeners(listenerClass).remove(listener);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// Prevent someone using this by accident.
		if (proxy != this.proxy) throw new UnsupportedOperationException();
		
		// Get the listeners for method's declaring class.
		Set<IAPIListener> listeners = getListeners(method.getDeclaringClass());
		
		// Execute all the listeners.
		for (IAPIListener listener : listeners) {
			try {
				method.invoke(listener, args);
			}
			catch (Throwable t) {
				// Immediately remove the listener from this dispatcher.
				// TODO: Remove the listener from this dispatcher.
				handleListenerFailure(t, listener);
			}
		}
		
		// Listener methods return void.
		return null;
	}
	
	@SuppressWarnings("static-method")
	private void handleListenerFailure(Throwable t, IAPIListener listener) throws Throwable {
		throw t;
	}
}
