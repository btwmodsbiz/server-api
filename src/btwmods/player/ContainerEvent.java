package btwmods.player;

import net.minecraft.src.Block;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;

public class ContainerEvent extends AbstractBlockEvent {
	
	public enum TYPE { OPENED, PLACED, REMOVED };

	private TYPE type;
	private Container container = null;
	
	public TYPE getType() {
		return type;
	}
	
	public Container getContainer() {
		return container;
	}
	
	public static ContainerEvent Open(EntityPlayer player, Block block, Container container, int x, int y, int z) {
		ContainerEvent event = new ContainerEvent(TYPE.OPENED, player);
		event.block = block;
		event.container = container;
		event.x = x;
		event.y = y;
		event.z = z;
		return event;
	}
	
	public static ContainerEvent Placed(EntityPlayer player, int x, int y, int z) {
		ContainerEvent event = new ContainerEvent(TYPE.PLACED, player);
		event.x = x;
		event.y = y;
		event.z = z;
		return event;
	}
	
	public static ContainerEvent Removed(EntityPlayer player, Block block, int metadata, int x, int y, int z) {
		ContainerEvent event = new ContainerEvent(TYPE.REMOVED, player);
		event.block = block;
		event.metadata = metadata;
		event.x = x;
		event.y = y;
		event.z = z;
		return event;
	}
	
	private ContainerEvent(TYPE type, EntityPlayer player) {
		super(player);
		this.type = type;
	}
}
