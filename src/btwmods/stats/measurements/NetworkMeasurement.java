package btwmods.stats.measurements;

import btwmods.measure.Measurement;
import btwmods.stats.NetworkType;

public class NetworkMeasurement extends Measurement<NetworkType> {
	
	public final int size;
	
	public NetworkMeasurement(NetworkType identifier, int size) {
		super(identifier);
		this.size = size;
	}
}
