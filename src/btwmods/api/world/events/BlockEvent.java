package btwmods.api.world.events;

import java.util.EventObject;

import net.minecraft.src.Block;
import net.minecraft.src.TileEntity;
import btwmods.api.world.WorldAPI;

public class BlockEvent extends EventObject {
	
	public enum TYPE { REMOVED_CONTAINER };

	private TYPE type;
	private WorldAPI api;
	private Block block;
	private TileEntity tileEntity = null;
	private int metadata = -1;
	private int x = -1;
	private int y = -1;
	private int z = -1;
	
	public TYPE getType() {
		return type;
	}
	
	public WorldAPI getApi() {
		return api;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public TileEntity getTileEntity() {
		if (tileEntity == null)
			tileEntity = api.world.getBlockTileEntity(getX(), getY(), getZ());
		
		return tileEntity;
	}
	
	public int getMetadata() {
		return metadata;
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
	
	public static BlockEvent RemovedContainer(WorldAPI api, Block block, int metadata, int x, int y, int z) {
		BlockEvent event = new BlockEvent(TYPE.REMOVED_CONTAINER, api);
		event.block = block;
		event.metadata = metadata;
		event.x = x;
		event.y = y;
		event.z = z;
		return event;
	}
	
	private BlockEvent(TYPE type, WorldAPI api) {
		super(api);
		this.type = type;
		this.api = api;
	}
}
