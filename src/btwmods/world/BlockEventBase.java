package btwmods.world;

import btwmods.events.PositionedEvent;
import net.minecraft.src.Block;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public abstract class BlockEventBase extends PositionedEvent {

	protected Block block = null;
	protected int metadata = -1;
	protected boolean metadataSet = false;
	
	private boolean checkedTileEntity = false;
	protected TileEntity tileEntity = null;
	protected ItemStack[] contents = null;
	
	public Block getBlock() {
		if (block == null) {
			block = Block.blocksList[world.getBlockId(x, y, z)];
		}
		
		return block;
	}
	
	public int getMetadata() {
		if (!metadataSet) {
			metadata = world.getBlockMetadata(x, y, z);
		}
		
		return metadata;
	}
	
	public TileEntity getTileEntity() {
		if (tileEntity == null && !checkedTileEntity) {
			tileEntity = world.getBlockTileEntity(x, y, z);
			checkedTileEntity = true;
		}
		
		return tileEntity;
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
