package btwmods;

public interface EventDispatcher {
	
	/**
	 * Add the listener to all the matching IAPIListeners supported by this dispatcher.
	 * 
	 * @throws IllegalArgumentException
	 */
	public void addListener(IAPIListener listener) throws IllegalArgumentException;
	
	/**
	 * Adds the listener for only the specified listener class.
	 * 
	 * @return true if the listener was added successfully; false if the listener does not
	 *         exist or is not not supported by this dispatcher.
	 * @throws IllegalArgumentException
	 */
	public boolean addListener(IAPIListener listener, Class listenerClass) throws IllegalArgumentException;
	
	/**
	 * Removes the listener to all the matching IAPIListeners supported by this dispatcher.
	 * 
	 * @throws IllegalArgumentException
	 */
	public void removeListener(IAPIListener listener) throws IllegalArgumentException;
	
	/**
	 * Removes the listener from only the specified listener class.
	 * 
	 * @return true if the listener was added successfully; false if the listener does not exist or is not not supported
	 *         by this dispatcher.
	 * @throws IllegalArgumentException
	 */
	public boolean removeListener(IAPIListener listener, Class listenerClass) throws IllegalArgumentException;
	
	/**
	 * Queue a listener to be added before any action that would read or change the listener list.
	 * Use this if you are adding queues to this EventDispatcher from another thread.
	 * 
	 * @see {@link #addListener(IAPIListener)}
	 * @throws IllegalArgumentException
	 */
	public void queuedAddListener(IAPIListener listener) throws IllegalArgumentException;
	
	/**
	 * Queue a listener to be added before any action that would read or change the listener list.
	 * Use this if you are adding queues to this EventDispatcher from another thread.
	 * 
	 * @see {@link #addListener(IAPIListener, Class)}
	 * @throws IllegalArgumentException
	 */
	public void queuedAddListener(IAPIListener listener, Class listenerClass) throws IllegalArgumentException;
	
	/**
	 * Queue a listener to be removed before any action that would read or change the listener list.
	 * Use this if you are removing queues to this EventDispatcher from another thread.
	 * 
	 * @see {@link #addListener(IAPIListener)}
	 * @throws IllegalArgumentException
	 */
	public void queuedRemoveListener(IAPIListener listener) throws IllegalArgumentException;
	
	/**
	 * Queue a listener to be removed before any action that would read or change the listener list.
	 * Use this if you are removing queues to this EventDispatcher from another thread.
	 * 
	 * @see {@link #addListener(IAPIListener, Class)}
	 * @throws IllegalArgumentException
	 */
	public void queuedRemoveListener(IAPIListener listener, Class listenerClass) throws IllegalArgumentException;
	
	/**
	 * Determine if there are any listeners for the specified IAPIListener class. 
	 * 
	 * @return true if there are; false otherwise.
	 */
	public boolean isEmpty(Class listenerClass);
}