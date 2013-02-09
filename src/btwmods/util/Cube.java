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
	
	@Override
	public boolean isWithin(int x, int y, int z) {
		return super.isWithin(x, y, z) && y >= y1 && y <= y2;
	}

	@Override
	public String toString() {
		return x1 + "," + y1 + "," + z1 + " to " + x2 + "," + y2 + "," + z2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + y1;
		result = prime * result + y2;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cube other = (Cube)obj;
		if (y1 != other.y1)
			return false;
		if (y2 != other.y2)
			return false;
		return true;
	}
}
