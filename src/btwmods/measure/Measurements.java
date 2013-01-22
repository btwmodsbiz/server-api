package btwmods.measure;

import java.util.ArrayDeque;

public class Measurements {
	
	private boolean enabled;
	private ArrayDeque<Measurement> measurements = new ArrayDeque<Measurement>();
	private ArrayDeque<TimeMeasurement> timeStack = new ArrayDeque<TimeMeasurement>();
	
	public Measurements() {
		this(true);
	}
	
	public Measurements(boolean isEnabled) {
		enabled = isEnabled;
	}
	
	public boolean completedMeasurements() {
		return timeStack.size() == 0;
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
	
	public ArrayDeque<Measurement> startNew() {
		ArrayDeque<Measurement> old = measurements;
		
		if ((enabled && measurements.size() > 0) || measurements.size() != 0) {
			measurements = new ArrayDeque<Measurement>();
			timeStack = new ArrayDeque<TimeMeasurement>();
		}
		
		return old;
	}
	
	public void record(Measurement measurement) {
		if (enabled)
			measurements.push(measurement);
	}
	
	public void begin(TimeMeasurement data) {
		if (enabled) {
			data.setParent(timeStack.peekLast());
			timeStack.push(data);
			data.start();
		}
	}
	
	public void end(Object identifier) {
		if (enabled) {
			Measurement popped = timeStack.pop().end();
			if (popped.identifier.equals(identifier)) {
				measurements.push(popped);
			}
			else {
				throw new IllegalStateException("Tried ending measurement " + popped.identifier.toString() + " as " + identifier.toString());
			}
		}
	}
}
