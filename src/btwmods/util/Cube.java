package btwmods.util;

public class Cube<Type> extends Area<Type> {
	public final int y1;
	public final int y2;
	
	public Cube(int x1, int y1, int z1, int x2, int y2, int z2) {
		super(x1, z1, x2, z2);
		this.y1 = y1;
		this.y2 = y2;
	}
	
	public Cube(int x1, int y1, int z1, int x2, int y2, int z2, Type data) {
		super(x1, z1, x2, z2, data);
		this.y1 = y1;
		this.y2 = y2;
	}
}
