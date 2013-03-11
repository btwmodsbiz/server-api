package btwmods.world;

import btwmods.events.PositionedEvent;
import net.minecraft.src.Block;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public abstract class BlockEventBase extends PositionedEvent {

	private boolean checkedBlockId = false;
	private int blockId = 0;
	private Block block = null;
	private int metadata = -1;
	private boolean metadataSet = false;
	
	private boolean checkedTileEntity = false;
	private TileEntity tileEntity = null;
	private ItemStack[] contents = null;
	
	public int getBlockId() {
		if (!checkedBlockId) {
			checkedBlockId = true;
			
			if (block != null) {
				blockId = block.blockID;
			}
			else {
				blockId = world.getBlockId(x, y, z);
				if (blockId > 0)
					block = Block.blocksList[blockId];
			}
		}
		
		return blockId;
	}
	
	protected void setBlockId(int blockId) {
		this.blockId = blockId;
		this.block = Block.blocksList[blockId];
		checkedBlockId = true;
	}
	
	public Block getBlock() {
		if (!checkedBlockId)
			getBlockId();
		
		return block;
	}
	
	protected void setBlock(Block block) {
		this.block = block;
		if (block != null)
			blockId = block.blockID;
		
		checkedBlockId = true;
	}
	
	public int getMetadata() {
		if (!metadataSet) {
			metadata = world.getBlockMetadata(x, y, z);
		}
		
		return metadata;
	}
	
	protected void setMetadata(int metadata) {
		this.metadata = metadata;
		metadataSet = true;
	}
	
	public TileEntity getTileEntity() {
		if (tileEntity == null && !checkedTileEntity) {
			tileEntity = world.getBlockTileEntity(x, y, z);
			checkedTileEntity = true;
		}
		
		return tileEntity;
	}
	
	protected void setTileEntity(TileEntity tileEntity) {
		this.tileEntity = tileEntity;
		checkedTileEntity = true;
	}
	
	public boolean hasInventory() {
		return getTileEntity() instanceof IInventory;
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
	
	protected BlockEventBase(Object source, World world, int x, int y, int z) {
		super(source, world, x, y, z);
	}
}
