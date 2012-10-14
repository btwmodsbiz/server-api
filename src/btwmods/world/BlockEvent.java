package btwmods.world;

import java.util.EventObject;

import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class BlockEvent extends EventObject {
	
	public enum TYPE { BROKEN };

	private TYPE type;
	private World world;
	private Chunk chunk;
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
	
	public World getWorld() {
		return world;
	}
	
	public Chunk getChunk() {
		return chunk;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public TileEntity getTileEntity() {
		if (!checkedTileEntity) {
			tileEntity = world.getBlockTileEntity(x, y, z);
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
	
	public static BlockEvent Broken(World world, Chunk chunk, Block block, int metadata, int x, int y, int z) {
		BlockEvent event = new BlockEvent(TYPE.BROKEN, world, chunk);
		event.block = block;
		event.metadata = metadata;
		event.x = x;
		event.y = y;
		event.z = z;
		return event;
	}
	
	private BlockEvent(TYPE type, World world, Chunk chunk) {
		super(chunk);
		this.type = type;
		this.world = world;
		this.chunk = chunk;
	}
}
