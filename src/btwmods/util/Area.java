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
