package btwmods.stats.measurements;

import btwmods.stats.Type;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class TileEntityUpdate extends LocationMeasurement {

	public final Class tileEntity;

	public TileEntityUpdate(World world, TileEntity tileEntity) {
		super(Type.TILE_ENTITY_UPDATE, world, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
		this.tileEntity = tileEntity.getClass();
	}
}
