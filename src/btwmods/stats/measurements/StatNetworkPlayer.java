package btwmods.stats.measurements;

import btwmods.network.NetworkType;

public class StatNetworkPlayer extends StatNetwork {

	public final String username;
	
	public StatNetworkPlayer(NetworkType identifier, String username, int size) {
		super(identifier, size);
		this.username = username;
	}
}
