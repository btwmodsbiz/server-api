package btwmods.api.player.events;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;

public class BlockEvent extends AbstractBlockEvent {
	
	public enum TYPE { ACTIVATED };
	
	private TYPE type;
	
	public TYPE getType() {
		return type;
	}
	
	public static BlockEvent Activated(EntityPlayer player, Block block, int x, int y, int z) {
		BlockEvent event = new BlockEvent(TYPE.ACTIVATED, player);
		event.block = block;
		event.x = x;
		event.y = y;
		event.z = z;
		return event;
	}
	
	private BlockEvent(TYPE type, EntityPlayer player) {
		super(player);
		this.type = type;
	}
}
