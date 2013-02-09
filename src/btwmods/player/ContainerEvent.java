package btwmods.player;

import net.minecraft.src.Block;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;

public class ContainerEvent extends PlayerBlockEventBase {
	
	public enum TYPE { OPENED, PLACED, REMOVED, CLOSED };

	private TYPE type;
	private Container container = null;
	
	public TYPE getType() {
		return type;
	}
	
	public Container getContainer() {
		return container;
	}
	
	public static ContainerEvent Open(EntityPlayer player, Block block, Container container, int x, int y, int z) {
		ContainerEvent event = new ContainerEvent(TYPE.OPENED, player, x, y, z);
		event.block = block;
		event.container = container;
		return event;
	}

	public static ContainerEvent Closed(EntityPlayer player, Container container) {
		ContainerEvent event = new ContainerEvent(TYPE.CLOSED, player, 0, 0, 0);
		event.container = container;
		return event;
	}
	
	public static ContainerEvent Placed(EntityPlayer player, int x, int y, int z) {
		ContainerEvent event = new ContainerEvent(TYPE.PLACED, player, x, y, z);
		return event;
	}
	
	public static ContainerEvent Removed(EntityPlayer player, Block block, int metadata, int x, int y, int z) {
		ContainerEvent event = new ContainerEvent(TYPE.REMOVED, player, x, y, z);
		event.block = block;
		event.metadata = metadata;
		return event;
	}
	
	private ContainerEvent(TYPE type, EntityPlayer player, int x, int y, int z) {
		super(player, player.worldObj, x, y, z);
		this.type = type;
	}
}
