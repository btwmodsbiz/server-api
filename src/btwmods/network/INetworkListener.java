package btwmods.network;

import btwmods.IAPIListener;

public interface INetworkListener extends IAPIListener {
	public void customPacketAction(CustomPacketEvent event);
}
