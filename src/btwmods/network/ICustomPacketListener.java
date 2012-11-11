package btwmods.network;

import btwmods.events.IAPIListener;

public interface ICustomPacketListener extends IAPIListener {
	public void onCustomPacket(CustomPacketEvent event);
}
