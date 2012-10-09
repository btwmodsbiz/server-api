package btwmods.api.world.events;

import java.util.EventObject;

import net.minecraft.src.Block;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import btwmods.api.world.WorldAPI;

public class BlockEvent extends EventObject {
	
	public enum TYPE { BROKEN };

	private TYPE type;
	private WorldAPI api;
	private Block block;
	private int metadata;
	private int x;
	private int y;
	private int z;
	
	private boolean checkedTileEntity = false;
	private TileEntity tileEntity = null;
	private ItemStack[] contents = null;
	
	public TYPE getType() {
		return type;
	}
	
	public WorldAPI getApi() {
		return api;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public TileEntity getTileEntity() {
		if (!checkedTileEntity) {
			tileEntity = api.world.getBlockTileEntity(x, y, z);
			checkedTileEntity = true;
		}
		
		return tileEntity;
	}
	
	public boolean hasInventory() {
		return contents != null || getTileEntity() instanceof IInventory;
	}
	
	public ItemStack[] getContents() {
		if (contents == null && hasInventory()) {
			IInventory inventory = (IInventory)getTileEntity();
			
			contents = new ItemStack[inventory.getSizeInventory()];
			for (int i = 0; i < contents.length; i++) {
				contents[i] = inventory.getStackInSlot(i);
			}
		}
		
		return contents;
	}
	
	public int getMetadata() {
		return metadata;
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
	
	public static BlockEvent Broken(WorldAPI api, Block block, int metadata, int x, int y, int z) {
		BlockEvent event = new BlockEvent(TYPE.BROKEN, api);
		event.block = block;
		event.metadata = metadata;
		event.x = x;
		event.y = y;
		event.z = z;
		return event;
	}
	
	private BlockEvent(TYPE type, WorldAPI api) {
		super(api);
		this.type = type;
		this.api = api;
	}
}
