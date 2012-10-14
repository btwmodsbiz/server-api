package btwmods.player;

import java.util.EventObject;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public class DropEvent extends EventObject {
	
	public enum TYPE { ONE, STACK, EJECT, ALL };

	private TYPE type;
	private EntityPlayer player;
	private ItemStack items = null;
	private int reduceTo = -1;
	
	public TYPE getType() {
		return type;
	}
	
	public EntityPlayer getPlayer() {
		return player;
	}
	
	public ItemStack getItems() {
		if (reduceTo != -1) {
			items = items.copy();
			items.stackSize = reduceTo;
			reduceTo = -1;
		}
		return items;
	}
	
	public static DropEvent One(EntityPlayer player, ItemStack items) {
		DropEvent event = new DropEvent(TYPE.ONE, player, items);
		event.reduceTo = 1;
		return event;
	}
	
	public static DropEvent Stack(EntityPlayer player, ItemStack items) {
		DropEvent event = new DropEvent(TYPE.STACK, player, items);
		return event;
	}
	
	public static DropEvent Eject(EntityPlayer player, ItemStack items) {
		DropEvent event = new DropEvent(TYPE.EJECT, player, items);
		return event;
	}
	
	public static DropEvent All(EntityPlayer player) {
		DropEvent event = new DropEvent(TYPE.ALL, player, null);
		return event;
	}
	
	private DropEvent(TYPE type, EntityPlayer player, ItemStack items) {
		super(player);
		this.type = type;
		this.player = player;
		this.items = items;
	}
}
