package btwmods.stats.measurements;

import btwmods.stats.Type;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class TileEntityUpdate extends WorldMeasurement {

	public final Class tileEntity;
	public final int x;
	public final int y;
	public final int z;
	public final int chunkX;
	public final int chunkZ;

	public TileEntityUpdate(World world, TileEntity tileEntity) {
		super(Type.TILE_ENTITY_UPDATE, world);
		this.tileEntity = tileEntity.getClass();
		x = tileEntity.xCoord;
		y = tileEntity.yCoord;
		z = tileEntity.zCoord;
		chunkX = x >> 4;
		chunkZ = z >> 4;
	}
}
