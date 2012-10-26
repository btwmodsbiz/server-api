package btwmods.measure;

public abstract class Measurement<E> {
	
	public final E identifier;
	
	public Measurement(E identifier) {
		this.identifier = identifier;
	}
}
