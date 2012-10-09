package btwmods.api.player.events;

import java.util.EventObject;

import net.minecraft.src.Block;
import net.minecraft.src.Container;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

import btwmods.api.player.PlayerAPI;

public class ContainerEvent extends EventObject {
	
	public enum TYPE { OPENED, PLACED, REMOVED };

	private TYPE type;
	private PlayerAPI api;
	private Container container = null;
	private Block block = null;
	private int metadata = -1;
	private World world = null;
	private int x = -1;
	private int y = -1;
	private int z = -1;
	
	private boolean checkedTileEntity = false;
	private TileEntity tileEntity = null;
	
	public TYPE getType() {
		return type;
	}
	
	public PlayerAPI getApi() {
		return api;
	}
	
	public Container getContainer() {
		return container;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public int getMetadata() {
		return metadata;
	}
	
	public World getWorld() {
		if (world == null)
			world = api.player.worldObj;
		
		return world;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public TileEntity getTileEntity() {
		if (!checkedTileEntity) {
			tileEntity = getWorld().getBlockTileEntity(x, y, z);
			checkedTileEntity = true;
		}
		
		return tileEntity;
	}
	
	public boolean hasInventory() {
		return getTileEntity() instanceof IInventory;
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
		this.api = api;
	}
}
