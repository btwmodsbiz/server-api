package btwmods.stats.measurements;

import net.minecraft.src.EntityPlayerMP;
import btwmods.network.NetworkType;

public class StatNetworkPlayer extends StatNetwork {

	public final EntityPlayerMP player;
	
	public StatNetworkPlayer(NetworkType identifier, EntityPlayerMP player, int size) {
		super(identifier, size);
		this.player = player;
	}
}
