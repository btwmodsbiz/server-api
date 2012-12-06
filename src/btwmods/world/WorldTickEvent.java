package btwmods.world;

import btwmods.events.APIEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.World;

public class WorldTickEvent extends APIEvent {

	public static WorldTickEvent StartTick(int worldIndex) {
		WorldTickEvent event = new WorldTickEvent(MinecraftServer.getServer(), TYPE.START, worldIndex);
		return event;
	}

	public static WorldTickEvent EndTick(int worldIndex) {
		WorldTickEvent event = new WorldTickEvent(MinecraftServer.getServer(), TYPE.END, worldIndex);
		return event;
	}
	
	public enum TYPE { START, END };

	private TYPE type;
	private int worldIndex;
	private World world = null;
	
	public TYPE getType() {
		return type;
	}
	
	public int getWorldIndex() {
		return worldIndex;
	}
	
	public World getWorld() {
		if (world == null)
			world = MinecraftServer.getServer().worldServers[worldIndex];
		
		return world;
	}

	private WorldTickEvent(Object source, TYPE type, int worldIndex) {
		super(source);
		this.type = type;
		this.worldIndex = worldIndex;
	}
}
