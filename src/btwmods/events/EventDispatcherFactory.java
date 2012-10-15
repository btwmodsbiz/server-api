package btwmods.events;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import btwmods.ModLoader;

public class EventDispatcherFactory implements InvocationHandler, EventDispatcher {

	public enum QueueAction { ADD, REMOVE };
	public class QueuedListener {
		public final QueueAction action;
		public final IAPIListener listener;
		public final Class listenerClass;
		
		public QueuedListener(QueueAction action, IAPIListener listener, Class listenerClass) {
			this.action = action;
			this.listener = listener;
			this.listenerClass = listenerClass;
		}
	}
	
	/**
	 * Create a new event dispatcher that supports the supplied IAPIListener classes.
	 * 
	 * @param listenerClasses the classes this dispatcher will support.
	 * @return a proxy that implements all the supplied IAPIListener.
	 */
	public static EventDispatcher create(Class<IAPIListener>[] listenerClasses) {
		if (listenerClasses == null || listenerClasses.length == 0)
			throw new IllegalArgumentException("listenerClasses cannot be null or an empty array");
		
		// Add the EventDispatcher interface to the end of the list.
		Class[] listenerClassesExtended = new Class[listenerClasses.length + 1];
		System.arraycopy(listenerClasses, 0, listenerClassesExtended, 0, listenerClasses.length);
		listenerClassesExtended[listenerClasses.length] = EventDispatcher.class;
		
		// Create a new factory as the handler and create the proxy.
		EventDispatcherFactory dispatcher = new EventDispatcherFactory(listenerClasses);
		dispatcher.proxy = Proxy.newProxyInstance(EventDispatcherFactory.class.getClassLoader(), listenerClassesExtended, dispatcher);
		return (EventDispatcher)dispatcher.proxy;
	}
	
	private Map<Class, Set<IAPIListener>> lookup = new HashMap<Class, Set<IAPIListener>>();
	private ConcurrentLinkedQueue<QueuedListener> listenerQueue = new ConcurrentLinkedQueue<QueuedListener>();
	private Class[] listenerClasses;
	private Object proxy;
	
	/**
	 * Initializes this dispatcher's lookup.
	 * @param listenerClasses
	 */
	private EventDispatcherFactory(Class[] listenerClasses) {
		this.listenerClasses = listenerClasses;
		init(listenerClasses);
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
	
	public boolean isEmpty(Class listenerClass) {
		Set<IAPIListener> listeners = listenerClass == null ? null : getListeners(listenerClass);
		return listeners == null || listeners.size() == 0;
	}
	
	// Note: If any other methods access 'lookup' then they should call processQueue() first.
	private Set<IAPIListener> getListeners(Class listenerClass) {
		processQueue();
		return lookup.get(listenerClass);
	}
	
	/**
	 * @see {@link EventDispatcher#addListener(IAPIListener)}
	 */
	public void addListener(IAPIListener listener) {
		isValidArgument(listener);
		Class listenerClass = listener.getClass();
		for (int i = 0; i < listenerClasses.length; i++) {
			if (listenerClasses[i].isAssignableFrom(listenerClass)) {
				addListenerInternal(listener, listenerClasses[i]);
			}
		}
	}

	/**
	 * @see {@link EventDispatcher#addListener(IAPIListener, Class)}
	 */
	public boolean addListener(IAPIListener listener, Class listenerClass) throws IllegalArgumentException {
		return checkIsSupportedListener(listener, listenerClass) && listenerClass.isAssignableFrom(proxy.getClass())
				&& addListenerInternal(listener, listenerClass);
	}

	private boolean addListenerInternal(IAPIListener listener, Class listenerClass) {
		return getListeners(listenerClass).add(listener);
	}
	
	/**
	 * @see {@link EventDispatcher#queuedAddListener(IAPIListener)}
	 */
	public void queuedAddListener(IAPIListener listener) {
		isValidArgument(listener);
		listenerQueue.add(new QueuedListener(QueueAction.ADD, listener, null));
	}
	
	/**
	 * @see {@link EventDispatcher#queuedAddListener(IAPIListener, Class)}
	 */
	public void queuedAddListener(IAPIListener listener, Class listenerClass) {
		if (checkIsSupportedListener(listener, listenerClass)) {
			listenerQueue.add(new QueuedListener(QueueAction.ADD, listener, listenerClass));
		}
	}

	/**
	 * @see {@link EventDispatcher#removeListener(IAPIListener)}
	 */
	public void removeListener(IAPIListener listener) {
		isValidArgument(listener);
		Class listenerClass = listener.getClass();
		for (int i = 0; i < listenerClasses.length; i++) {
			if (listenerClasses[i].isAssignableFrom(listenerClass)) {
				removeListenerInternal(listener, listenerClasses[i]);
			}
		}
	}

	/**
	 * @see {@link EventDispatcher#removeListener(IAPIListener, Class)}
	 */
	public boolean removeListener(IAPIListener listener, Class listenerClass) throws IllegalArgumentException {
		return checkIsSupportedListener(listener, listenerClass) && listenerClass.isAssignableFrom(proxy.getClass())
				&& removeListenerInternal(listener, listenerClass);
	}

	private boolean removeListenerInternal(IAPIListener listener, Class listenerClass) {
		return getListeners(listenerClass).remove(listener);
	}

	/**
	 * @see {@link EventDispatcher#queuedRemoveListener(IAPIListener)}
	 */
	public void queuedRemoveListener(IAPIListener listener) {
		isValidArgument(listener);
		listenerQueue.add(new QueuedListener(QueueAction.REMOVE, listener, null));
	}

	/**
	 * @see {@link EventDispatcher#queuedRemoveListener(IAPIListener, Class)}
	 */
	public void queuedRemoveListener(IAPIListener listener, Class listenerClass) {
		if (checkIsSupportedListener(listener, listenerClass)) {
			listenerQueue.add(new QueuedListener(QueueAction.REMOVE, listener, listenerClass));
		}
	}
	
	@SuppressWarnings("static-method")
	private boolean isValidArgument(IAPIListener listener) throws IllegalArgumentException {
		if (listener == null)
			throw new IllegalArgumentException("listener cannot be null");
		
		return true;
	}
	
	@SuppressWarnings("static-method")
	private boolean areValidArguments(IAPIListener listener, Class listenerClass) throws IllegalArgumentException {
		if (listener == null)
			throw new IllegalArgumentException("listener cannot be null");
		
		if (listenerClass == null)
			throw new IllegalArgumentException("listenerClass cannot be null");
		
		if (!IAPIListener.class.isAssignableFrom(listenerClass))
			throw new IllegalArgumentException("listenerClass is not an instance of IAPIListener");

		if (!listenerClass.isAssignableFrom(listener.getClass()))
			throw new IllegalArgumentException("listener is not an instance of listenerClass");
		
		return true;
	}
	
	private boolean checkIsSupportedListener(IAPIListener listener, Class listenerClass) throws IllegalArgumentException {
		return areValidArguments(listener, listenerClass) && listenerClass.isAssignableFrom(proxy.getClass());
	}
	
	private void processQueue() {
		QueuedListener entry;
		while ((entry = listenerQueue.poll()) != null) {
			if (entry.action == QueueAction.ADD) {
				if (entry.listenerClass == null)
					addListener(entry.listener);
				else
					addListener(entry.listener, entry.listenerClass);
			}
			else if (entry.action == QueueAction.REMOVE)
				if (entry.listenerClass == null)
					removeListener(entry.listener);
				else
					removeListener(entry.listener, entry.listenerClass);
		}
	}
	
	public boolean isSupportedListener(IAPIListener listener) {
		isValidArgument(listener);
		Class listenerClass = listener.getClass();
		for (int i = 0; i < listenerClasses.length; i++) {
			if (listenerClasses[i].isAssignableFrom(listenerClass)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Class declaringClass = method.getDeclaringClass();
		
		if (declaringClass == EventDispatcher.class) {
			return method.invoke(this, args);
		}
		else {
			// Process any queued failures before invoking a method on a mod.
			ModLoader.processFailureQueue();
			
			// Get the listeners for method's declaring class.
			Set<IAPIListener> listeners = getListeners(method.getDeclaringClass());
			
			// Execute all the listeners.
			for (IAPIListener listener : listeners) {
				try {
					method.invoke(listener, args);
					
					// Stop processing events if there is an event argument that has flagged for the event handling to stop.
					if (args.length > 0 && args[0] instanceof IEventInterrupter && ((IEventInterrupter)args[0]).isInterrupted())
						break;
				}
				catch (InvocationTargetException e) {
					removeListener(listener);
					handleListenerFailure(e.getCause(), listener);
				} catch (Throwable e) {
					removeListener(listener);
					handleListenerFailure(e.getCause(), listener);
				}
			}
			
			// Listener methods return void.
			return null;
		}
	}
	
	@SuppressWarnings("static-method")
	protected void handleListenerFailure(Throwable t, IAPIListener listener) {
		ModLoader.reportListenerFailure(t, listener);
	}
}
