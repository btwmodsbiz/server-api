package btwmods.server;

import java.util.EventObject;
import net.minecraft.server.MinecraftServer;

public class TickEvent extends EventObject {
	
	private static MinecraftServer server;
	
	public enum TYPE { START, END };

	private TYPE type;
	private int tickCounter;
	
	public TYPE getType() {
		return type;
	}
	
	public int getTickCounter() {
		return tickCounter;
	}

	private TickEvent(Object source, TYPE type) {
		super(source);
		this.type = type;
	}

	public static TickEvent StartTick(int tickCounter) {
		if (server == null) server = MinecraftServer.getServer();
		TickEvent event = new TickEvent(server, TYPE.START);
		event.tickCounter = tickCounter;
		return event;
	}

	public static TickEvent EndTick(int tickCounter) {
		if (server == null) server = MinecraftServer.getServer();
		TickEvent event = new TickEvent(server, TYPE.END);
		event.tickCounter = tickCounter;
		return event;
	}

}
