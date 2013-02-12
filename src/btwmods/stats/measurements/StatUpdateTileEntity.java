package btwmods.stats.measurements;

import btwmods.stats.Type;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class StatUpdateTileEntity extends StatPositioned {

	public final Class tileEntity;

	public StatUpdateTileEntity(World world, TileEntity tileEntity) {
		super(Type.TILE_ENTITY_UPDATE, world, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
		this.tileEntity = tileEntity.getClass();
	}
}
