package btwmods.events;

import net.minecraft.src.World;

public abstract class WorldEvent extends APIEvent {
	
	protected final World world;
	
	public World getWorld() {
		return world;
	}
	
	protected WorldEvent(Object source, World world) {
		super(source);
		
		if (world == null)
			throw new NullPointerException("world cannot be null.");
		
		this.world = world;
	}
}
