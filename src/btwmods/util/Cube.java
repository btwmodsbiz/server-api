package btwmods.util;

import btwmods.io.Settings;

public class Cube extends Area {
	public final int y1;
	public final int y2;
	
	public Cube(int x1, int y1, int z1, int x2, int y2, int z2) {
		super(x1, z1, x2, z2);
		this.y1 = y1;
		this.y2 = y2;
	}
	
	public Cube(int x1, int y1, int z1, int x2, int y2, int z2, String name) {
		super(x1, z1, x2, z2, name);
		this.y1 = y1;
		this.y2 = y2;
	}
	
	public Cube(int x1, int y1, int z1, int x2, int y2, int z2, Settings settings) {
		super(x1, z1, x2, z2, settings);
		this.y1 = y1;
		this.y2 = y2;
	}
	
	public Cube(int x1, int y1, int z1, int x2, int y2, int z2, String name, Settings settings) {
		super(x1, z1, x2, z2, name, settings);
		this.y1 = y1;
		this.y2 = y2;
	}
}
