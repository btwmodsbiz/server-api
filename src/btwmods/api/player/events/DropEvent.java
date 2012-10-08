package btwmods.api.player.events;

import java.util.EventObject;

import btwmods.api.player.PlayerAPI;
import net.minecraft.src.ItemStack;

public class DropEvent extends EventObject {
	
	public enum TYPE { ONE, STACK, EJECT, ALL };

	private TYPE type;
	private PlayerAPI api;
	private ItemStack items = null;
	private int reduceTo = -1;
	
	public TYPE getType() {
		return type;
	}
	
	public PlayerAPI getApi() {
		return api;
	}
	
	public ItemStack getItems() {
		if (reduceTo != -1) {
			items = items.copy();
			items.stackSize = reduceTo;
			reduceTo = -1;
		}
		return items;
	}
	
	public static DropEvent One(PlayerAPI api, ItemStack items) {
		DropEvent event = new DropEvent(TYPE.ONE, api, items);
		event.reduceTo = 1;
		return event;
	}
	
	public static DropEvent Stack(PlayerAPI api, ItemStack items) {
		DropEvent event = new DropEvent(TYPE.STACK, api, items);
		return event;
	}
	
	public static DropEvent Eject(PlayerAPI api, ItemStack items) {
		DropEvent event = new DropEvent(TYPE.EJECT, api, items);
		return event;
	}
	
	public static DropEvent All(PlayerAPI api) {
		DropEvent event = new DropEvent(TYPE.ALL, api, null);
		return event;
	}
	
	private DropEvent(TYPE type, PlayerAPI api, ItemStack items) {
		super(api);
		this.type = type;
		this.api = api;
		this.items = items;
	}
}
