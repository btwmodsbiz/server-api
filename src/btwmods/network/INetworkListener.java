package btwmods.network;

import btwmods.events.IAPIListener;

public interface INetworkListener extends IAPIListener {
	public void customPacketAction(CustomPacketEvent event);
}
