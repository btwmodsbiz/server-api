package btwmods.api.player.events;

import java.util.EventObject;

import net.minecraft.src.Block;
import net.minecraft.src.IInventory;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import btwmods.api.player.PlayerAPI;

public class BlockEvent extends AbstractBlockEvent {
	
	public enum TYPE { ACTIVATED };
	
	private TYPE type;
	
	public TYPE getType() {
		return type;
	}
	
	public static BlockEvent Activated(PlayerAPI api, Block block, int x, int y, int z) {
		BlockEvent event = new BlockEvent(TYPE.ACTIVATED, api);
		event.block = block;
		event.x = x;
		event.y = y;
		event.z = z;
		return event;
	}
	
	private BlockEvent(TYPE type, PlayerAPI api) {
		super(api);
		this.type = type;
	}
}
