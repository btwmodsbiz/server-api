package btwmods.util;

import btwmods.io.Settings;

public class Area {
	public final int x1;
	public final int x2;
	public final int z1;
	public final int z2;
	public final String name;
	public final Settings settings;
	
	public Area(int x1, int z1 ,int x2, int z2) {
		this(x1, z1, x2, z2, null, null);
	}
	
	public Area(int x1, int z1 ,int x2, int z2, String name) {
		this(x1, z1, x2, z2, name, null);
	}
	
	public Area(int x1, int z1 ,int x2, int z2, Settings settings) {
		this(x1, z1, x2, z2, null, settings);
	}
	
	public Area(int x1, int z1 ,int x2, int z2, String name, Settings settings) {
		this.x1 = x1;
		this.x2 = x2;
		this.z1 = z1;
		this.z2 = z2;
		this.name = name;
		this.settings = settings;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x1;
		result = prime * result + x2;
		result = prime * result + z1;
		result = prime * result + z2;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Area))
			return false;
		Area other = (Area)obj;
		if (x1 != other.x1)
			return false;
		if (x2 != other.x2)
			return false;
		if (z1 != other.z1)
			return false;
		if (z2 != other.z2)
			return false;
		return true;
	}
}
