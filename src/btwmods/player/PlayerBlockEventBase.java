package btwmods.player;

import btwmods.world.BlockEventBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class PlayerBlockEventBase extends BlockEventBase {
	
	private EntityPlayer player;
	
	public EntityPlayer getPlayer() {
		return player;
	}

	protected PlayerBlockEventBase(EntityPlayer player, World world, int x, int y, int z) {
		super(player, world, x, y, z);
		this.player = player;
	}
}
