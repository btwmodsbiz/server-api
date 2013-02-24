package btwmods.measure;

public class TimeMeasurement<E> extends Measurement<E> {
	
	private long time = -1;
	private TimeMeasurement parent = null;
	
	public long getTime() {
		return time;
	}
	
	public TimeMeasurement getParent() {
		return parent;
	}
	
	public void setParent(TimeMeasurement parent) {
		this.parent = parent;
	}
	
	public TimeMeasurement(E identifier) {
		super(identifier);
	}
	
	public void start() {
		this.time = System.nanoTime();
	}
	
	public TimeMeasurement<E> end() {
		time = System.nanoTime() - time;
		return this;
	}
	
	public TimeMeasurement<E> record(long time) {
		this.time = time;
		return this;
	}
}
