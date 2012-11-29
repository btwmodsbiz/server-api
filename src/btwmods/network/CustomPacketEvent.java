package btwmods.network;

import btwmods.events.APIEvent;

public class CustomPacketEvent extends APIEvent {
	
	private String channel;
	private byte[] data;
	private int length;
	private boolean isHandled = true;
	
	public String getChannel() {
		return channel;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public int getLength() {
		return length;
	}
	
	public boolean isHandled() {
		return isHandled;
	}
	
	public void setNotHandled() {
		isHandled = false;
	}
	
	public CustomPacketEvent(String channel, byte[] data, int length) {
		super(channel);
		this.channel = channel;
		this.data = data;
		this.length = length;
	}
}
