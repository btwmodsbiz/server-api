package btwmods.stats.measurements;

import net.minecraft.src.EntityPlayerMP;
import btwmods.network.NetworkType;

public class PlayerNetworkMeasurement extends NetworkMeasurement {

	public final EntityPlayerMP player;
	
	public PlayerNetworkMeasurement(NetworkType identifier, EntityPlayerMP player, int size) {
		super(identifier, size);
		this.player = player;
	}
}
