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
	protected int GetChanceOfNeighborsEncouragingFireCustom(World world, int x, int y, int z) {
		int chance = super.GetChanceOfNeighborsEncouragingFireCustom(world, x, y, z);
		return chance != 0 && ModBlockFire.chanceOfNeighborsEncouragingFireAllowed(world, x, y, z) ? chance : 0;
	}

	@Override
	public boolean canBlockCatchFire(IBlockAccess blockAccess, int x, int y, int z) {
		return super.canBlockCatchFire(blockAccess, x, y, z) && (!(blockAccess instanceof World) || ModBlockFire.blockCanCatchFire((World)blockAccess, x, y, z));
	}

	@Override
	protected void TryToDestroyBlockWithFire(World world, int x, int y, int z, int var5, Random rnd, int var7) {
		if (ModBlockFire.destroyBlockWithFireAllowed(world, x, y, z))
			super.TryToDestroyBlockWithFire(world, x, y, z, var5, rnd, var7);
	}
}
