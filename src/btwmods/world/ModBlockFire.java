package btwmods.world;

import java.util.Random;

import btwmods.WorldAPI;

import net.minecraft.src.FCBlockFire;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

public class ModBlockFire extends FCBlockFire {

	public ModBlockFire(int var1) {
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
	
	public static boolean chanceOfNeighborsEncouragingFireAllowed(World world, int x, int y, int z) {
		return WorldAPI.onGetChanceOfNeighborsEncouragingFireAllowed(world, x, y, z);
	}
	
	public static boolean destroyBlockWithFireAllowed(World world, int x, int y, int z) {
		int blockId = world.getBlockId(x, y, z);
		return blockId <= 0 || !CanBlockBeDestroyedByFire(blockId) || WorldAPI.onDestroyBlockWithFireAttempt(world, blockId, x, y, z);
	}
	
	public static boolean blockCanCatchFire(World world, int x, int y, int z) {
		return WorldAPI.onIsFlammableBlock(world, x, y, z);
	}
}
