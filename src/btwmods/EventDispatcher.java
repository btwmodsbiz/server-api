package btwmods;

public interface EventDispatcher {
	public void addListener(IAPIListener listener);
	public boolean addListener(IAPIListener listener, Class listenerClass);
	public void removeListener(IAPIListener listener);
	public boolean removeListener(IAPIListener listener, Class listenerClass);
}