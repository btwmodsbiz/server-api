package btwmods.api.player.events;

import java.util.EventObject;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public abstract class AbstractBlockEvent extends EventObject {

	protected EntityPlayer player;
	protected Block block = null;
	protected int metadata = -1;
	protected World world = null;
	protected int x = -1;
	protected int y = -1;
	protected int z = -1;
	
	protected boolean checkedTileEntity = false;
	protected TileEntity tileEntity = null;
	
	public EntityPlayer getPlayer() {
		return player;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public int getMetadata() {
		return metadata;
	}
	
	public World getWorld() {
		if (world == null)
			world = player.worldObj;
		
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
	
	protected AbstractBlockEvent(EntityPlayer player) {
		super(player);
		this.player = player;
	}
}
