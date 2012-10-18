package btwmods.player;

import btwmods.events.IEventInterrupter;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;

public class BlockEvent extends AbstractBlockEvent implements IEventInterrupter {
	
	public static BlockEvent Activated(EntityPlayer player, Block block, int x, int y, int z) {
		BlockEvent event = new BlockEvent(TYPE.ACTIVATED, player);
		event.block = block;
		event.x = x;
		event.y = y;
		event.z = z;
		return event;
	}

	public static BlockEvent ActivationAttempt(EntityPlayer player, Block block, int x, int y, int z) {
		BlockEvent event = new BlockEvent(TYPE.ACTIVATION_ATTEMPT, player);
		event.block = block;
		event.x = x;
		event.y = y;
		event.z = z;
		return event;
	}
	
	public enum TYPE { ACTIVATED, ACTIVATION_ATTEMPT };
	
	private TYPE type;
	private boolean activationHandled;
	
	public TYPE getType() {
		return type;
	}
	
	public boolean isActivationHandled() {
		return type == TYPE.ACTIVATION_ATTEMPT && activationHandled;
	}
	
	public void markActivationHandled() {
		activationHandled = true;
	}
	
	private BlockEvent(TYPE type, EntityPlayer player) {
		super(player);
		this.type = type;
	}

	@Override
	public boolean isInterrupted() {
		return isActivationHandled();
	}
}
