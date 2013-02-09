package btwmods.server;

import btwmods.events.APIEvent;
import net.minecraft.server.MinecraftServer;

public class ServerStopEvent extends APIEvent {
	
	public enum TYPE { PRE, POST };

	private TYPE type;
	
	public TYPE getType() {
		return type;
	}

	private ServerStopEvent(Object source, TYPE type) {
		super(source);
		this.type = type;
	}

	public static ServerStopEvent Pre() {
		return new ServerStopEvent(MinecraftServer.getServer(), TYPE.PRE);
	}

	public static ServerStopEvent Post() {
		return new ServerStopEvent(MinecraftServer.getServer(), TYPE.POST);
	}

}
