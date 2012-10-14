package btwmods.server;

public abstract class Measurement<E> {
	
	public final E identifier;
	private long time;
	
	public long getTime() {
		return time;
	}
	
	public Measurement(E identifier) {
		this.identifier = identifier;
		this.time = System.nanoTime();
	}
	
	public Measurement<E> end() {
		time = System.nanoTime() - time;
		return this;
	}
}
