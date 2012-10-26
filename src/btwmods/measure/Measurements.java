package btwmods.measure;

import java.util.ArrayDeque;

public class Measurements<T extends TimeMeasurement> {
	
	private boolean enabled;
	private ArrayDeque<T> measurements = new ArrayDeque<T>();
	private ArrayDeque<T> dataStack = new ArrayDeque<T>();
	
	public Measurements() {
		this(true);
	}
	
	public Measurements(boolean isEnabled) {
		enabled = isEnabled;
	}
	
	public boolean completedMeasurements() {
		return dataStack.size() == 0;
	}
	
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
	
	public void end(Object identifier) {
		if (enabled) {
			T popped = (T)dataStack.pop().end();
			if (popped.identifier.equals(identifier)) {
				measurements.push(popped);
			}
			else {
				throw new IllegalStateException("Tried ending measurement " + popped.identifier.toString() + " as " + identifier.toString());
			}
		}
	}
}
