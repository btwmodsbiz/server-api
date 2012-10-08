package btwmods.api.player.listeners;

import net.minecraft.src.Block;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public interface IContainerListener {
	public void containerPlaced(Container container, World world, int x, int y, int z);
	public void containerOpened(Block block, Container container, World world, int x, int y, int z);
	public void containerClosed(Container container);
	public void containerDestroyed(Container container, int x, int y, int z);
	//public void containerContentsEjected(Container container, int x, int y, int z);
}
