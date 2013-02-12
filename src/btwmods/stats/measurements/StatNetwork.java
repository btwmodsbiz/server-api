package btwmods.stats.measurements;

import btwmods.measure.Measurement;
import btwmods.network.NetworkType;

public class StatNetwork extends Measurement<NetworkType> {
	
	public final int size;
	
	public StatNetwork(NetworkType identifier, int size) {
		super(identifier);
		this.size = size;
	}
}
