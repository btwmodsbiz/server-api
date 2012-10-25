package btwmods.network;

import java.util.EventObject;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.Packet;

public class PacketEvent extends EventObject {
	
	private EntityPlayerMP player = null;
	private NetServerHandler netServerHandler = null;
	private Packet packet;
	
	public EntityPlayerMP getPlayer() {
		return player;
	}
	
	public Packet getPacket() {
		return packet;
	}
	
	public NetServerHandler getNetServerHandler() {
		return netServerHandler;
	}
	
	public static PacketEvent ReceivedPlayerPacket(EntityPlayerMP player, Packet packet, NetServerHandler netHandler) {
		PacketEvent event = new PacketEvent(player, packet, netHandler);
		return event;
	}

	private PacketEvent(EntityPlayerMP player, Packet packet, NetServerHandler netServerHandler) {
		super(player);
		this.player = player;
		this.packet = packet;
		this.netServerHandler = netServerHandler;
	}
}
