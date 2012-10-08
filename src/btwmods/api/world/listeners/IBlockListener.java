package btwmods.api.world.listeners;

import net.minecraft.src.Block;
import net.minecraft.src.TileEntity;

public interface IBlockListener {
	public void blockContainerBroken(Block block, int blockMetadata, TileEntity tileEntity, int x, int y, int z);
}
