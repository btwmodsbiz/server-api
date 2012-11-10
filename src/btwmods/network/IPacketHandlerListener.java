package btwmods.network;

import btwmods.events.IAPIListener;

public interface IPacketHandlerListener extends IAPIListener {
	public void onHandlePacket(PacketHandlerEvent event);
}
