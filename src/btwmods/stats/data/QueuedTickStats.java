package btwmods.stats.data;

import java.util.ArrayDeque;

import btwmods.measure.Measurement;

public class QueuedTickStats {
	public long tickEnd;
	public int tickCounter;
	public long tickTime;
	public String[] players;
	public long sentPacketCount;
	public long sentPacketSize;
	public long receivedPacketCount;
	public long receivedPacketSize;
	public long[] worldTickTimes;
	public int[] loadedChunks;
	public int[] id2ChunkMap;
	public int[] droppedChunksSet;
	public int[] loadedEntityList;
	public int[] loadedTileEntityList;
	public int[] trackedEntities;
	public ArrayDeque<Measurement> measurements;
	public long bytesSent;
	public long bytesReceived;
	public long handlerInvocations;
}