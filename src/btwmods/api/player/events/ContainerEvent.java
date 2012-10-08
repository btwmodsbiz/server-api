package btwmods.api.player.events;

import java.util.EventObject;

import net.minecraft.src.Block;
import net.minecraft.src.Container;
import net.minecraft.src.World;

import btwmods.api.player.PlayerAPI;

public class ContainerEvent extends EventObject {
	
	public enum TYPE { OPENED, CLOSED, PLACED, DESTROYED };

	private TYPE type;
	private PlayerAPI api;
	private Container container = null;
	private World world = null;
	private int x = -1;
	private int y = -1;
	private int z = -1;
	
	public TYPE getType() {
		return type;
	}
	
	public PlayerAPI getApi() {
		return api;
	}
	
	public static ContainerEvent Open(PlayerAPI api, Block block, Container container, World world, int x, int y, int z) {
		ContainerEvent event = new ContainerEvent(TYPE.OPENED, api);
		event.container = container;
		event.world = world;
		event.x = x;
		event.y = y;
		event.z = z;
		return event;
	}
	
	public static ContainerEvent Close(PlayerAPI api, Container container) {
		ContainerEvent event = new ContainerEvent(TYPE.CLOSED, api);
		event.container = container;
		return event;
	}
	
	public static ContainerEvent Placed(PlayerAPI api) {
		ContainerEvent event = new ContainerEvent(TYPE.PLACED, api);
		return event;
	}
	
	public static ContainerEvent Destroyed(PlayerAPI api) {
		ContainerEvent event = new ContainerEvent(TYPE.DESTROYED, api);
		return event;
	}
	
	private ContainerEvent(TYPE type, PlayerAPI api) {
		super(api);
		this.type = type;
		this.api = api;
	}
}
