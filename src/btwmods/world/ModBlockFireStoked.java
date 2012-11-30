package btwmods.world;

import java.util.Random;

import net.minecraft.src.FCBlockFireStoked;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

public class ModBlockFireStoked extends FCBlockFireStoked {

	public ModBlockFireStoked(int var1) {
		super(var1);
	}

	@Override
	public boolean canBlockCatchFire(IBlockAccess var1, int var2, int var3, int var4) {
		return super.canBlockCatchFire(var1, var2, var3, var4);
	}

	@Override
	protected void TryToDestroyBlockWithFire(World world, int x, int y, int z, int var5, Random rnd, int var7) {
		if (ModBlockFire.destroyBlockWithFireAllowed(world, x, y, z))
			super.TryToDestroyBlockWithFire(world, x, y, z, var5, rnd, var7);
	}
}
