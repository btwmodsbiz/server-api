package btwmods.measure;

import java.util.ArrayDeque;

public class Measurements<T extends Measurement> {
	
	private boolean enabled = true;
	private ArrayDeque<T> measurements = new ArrayDeque<T>();
	private ArrayDeque<T> dataStack = new ArrayDeque<T>();
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean value) {
		enabled = value;
		if (!enabled) {
			startNew();
		}
	}
	
	public ArrayDeque<T> startNew() {
		ArrayDeque<T> old = measurements;
		
		if ((enabled && measurements.size() > 0) || measurements.size() != 0) {
			measurements = new ArrayDeque<T>();
			dataStack = new ArrayDeque<T>();
		}
		
		return old;
	}
	
	public void begin(T data) {
		if (enabled) {
			dataStack.push(data);
			data.start();
		}
	}
	
	public void end() {
		if (enabled)
			measurements.push((T)dataStack.pop().end());
	}
}
