package btwmods.network;

import java.util.EventObject;

import btwmods.events.IEventInterrupter;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Packet;

public class PacketHandlerEvent extends EventObject implements IEventInterrupter {
	
	private EntityPlayerMP player = null;
	private Packet packet;
	private boolean sendHandled = false;
	
	public EntityPlayerMP getPlayer() {
		return player;
	}
	
	public Packet getPacket() {
		return packet;
	}
	
	public void abortSend() {
		sendHandled = true;
		packet = null;
	}
	
	public void replaceWithPacket(Packet packet) {
		this.packet = packet;
		sendHandled = true;
	}

	public PacketHandlerEvent(Object source) {
		super(source);
	}

	public static PacketHandlerEvent SendPlayerPacket(EntityPlayerMP player, Packet packet) {
		PacketHandlerEvent event = new PacketHandlerEvent(player, packet);
		return event;
	}

	private PacketHandlerEvent(EntityPlayerMP player, Packet packet) {
		super(player);
		this.player = player;
		this.packet = packet;
	}

	@Override
	public boolean isInterrupted() {
		return sendHandled;
	}
}
