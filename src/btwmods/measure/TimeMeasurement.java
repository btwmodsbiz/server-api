package btwmods.measure;

public abstract class TimeMeasurement<E> {
	
	public final E identifier;
	private long time;
	
	public long getTime() {
		return time;
	}
	
	public TimeMeasurement(E identifier) {
		this.identifier = identifier;
	}
	
	public void start() {
		this.time = System.nanoTime();
	}
	
	public TimeMeasurement<E> end() {
		time = System.nanoTime() - time;
		return this;
	}
}
