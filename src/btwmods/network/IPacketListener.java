package btwmods.network;

import btwmods.events.IAPIListener;

public interface IPacketListener extends IAPIListener {
	public void onPacket(PacketEvent event);
}
