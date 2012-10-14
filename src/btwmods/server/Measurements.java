package btwmods.server;

import java.util.ArrayDeque;

public class Measurements<T extends Measurement> {
	
	private boolean enabled = true;
	private ArrayDeque<T> measurements;
	private ArrayDeque<T> dataStack;
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean value) {
		enabled = value;
		if (!enabled) {
			dataStack = null;
			measurements = null;
		}
	}
	
	public Measurements() {
		startNew();
	}
	
	public ArrayDeque<T> startNew() {
		if (enabled) {
			ArrayDeque<T> old = measurements;
			measurements = new ArrayDeque<T>();
			dataStack = new ArrayDeque<T>();
			return old;
		}
		else {
			return null;
		}
	}
	
	public void begin(T data) {
		if (enabled && measurements != null) {
			dataStack.push(data);
			data.start();
		}
	}
	
	public void end() {
		if (enabled && measurements != null)
			measurements.push((T)dataStack.pop().end());
	}
}
