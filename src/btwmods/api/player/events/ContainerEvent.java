package btwmods.api.player.events;

import java.util.EventObject;

import net.minecraft.src.Block;
import net.minecraft.src.Container;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

import btwmods.api.player.PlayerAPI;

public class ContainerEvent extends AbstractBlockEvent {
	
	public enum TYPE { OPENED, PLACED, REMOVED };

	private TYPE type;
	private Container container = null;
	
	private boolean checkedTileEntity = false;
	private TileEntity tileEntity = null;
	
	public TYPE getType() {
		return type;
	}
	
	public Container getContainer() {
		return container;
	}
	
	public static ContainerEvent Open(PlayerAPI api, Block block, Container container, int x, int y, int z) {
		ContainerEvent event = new ContainerEvent(TYPE.OPENED, api);
		event.block = block;
		event.container = container;
		event.x = x;
		event.y = y;
		event.z = z;
		return event;
	}
	
	public static ContainerEvent Placed(PlayerAPI api, int x, int y, int z) {
		ContainerEvent event = new ContainerEvent(TYPE.PLACED, api);
		event.x = x;
		event.y = y;
		event.z = z;
		return event;
	}
	
	public static ContainerEvent Removed(PlayerAPI api, Block block, int metadata, int x, int y, int z) {
		ContainerEvent event = new ContainerEvent(TYPE.REMOVED, api);
		event.block = block;
		event.metadata = metadata;
		event.x = x;
		event.y = y;
		event.z = z;
		return event;
	}
	
	private ContainerEvent(TYPE type, PlayerAPI api) {
		super(api);
		this.type = type;
	}
}
