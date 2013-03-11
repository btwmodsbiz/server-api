package btwmods.util;

public class Area<Type> {
	public final int x1;
	public final int x2;
	public final int z1;
	public final int z2;
	public final Type data;
	
	public Area(int x1, int z1 ,int x2, int z2) {
		this(x1, z1, x2, z2, null);
	}
	
	public Area(int x1, int z1, int x2, int z2, Type data) {
		this.x1 = x1;
		this.x2 = x2;
		this.z1 = z1;
		this.z2 = z2;
		this.data = data;
	}
	
	public boolean isWithin(int x, int z) {
		return x >= x1 && x <= x2 && z >= z1 && z <= z2;
	}
	
	/**
	 * @param x The X coordinate to check.
	 * @param y The Y coordinate to check.
	 * @param z The Z coordinate to check.
	 * @return true if within the Area (or Cube); false otherwise.
	 */
	public boolean isWithin(int x, int y, int z) {
		return isWithin(x, z);
	}
	
	public Area<Type> clone(Type data) {
		return new Area<Type>(x1, x2, z1, z2, data);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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

	@Override
	public String toString() {
		return x1 + "," + z1 + " to " + x2 + "," + z2;
	}
}
