package btwmods.stats.measurements;

import btwmods.measure.Measurement;

public class PlayerPosition extends Measurement<String> {
	
	public final int worldIndex;
	public final double x;
	public final double y;
	public final double z;

	public PlayerPosition(String username, int worldIndex, double x, double y, double z) {
		super(username);
		this.worldIndex = worldIndex;
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
