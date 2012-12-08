package btwmods.world;

import btwmods.events.IAPIListener;

public interface IChunkListener extends IAPIListener {
	public void onChunkAction(ChunkEvent event);
}
