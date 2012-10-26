package btwmods.measure;

public class TimeMeasurement<E> extends Measurement<E> {
	
	private long time;
	
	public long getTime() {
		return time;
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
}
