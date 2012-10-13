package btwmods.network;

import java.util.EventObject;

public class CustomPacketEvent extends EventObject {
	
	private String channel;
	private byte[] data;
	private int length;
	
	public CustomPacketEvent(String channel, byte[] data, int length) {
		super(channel);
		this.channel = channel;
		this.data = data;
		this.length = length;
	}
}
