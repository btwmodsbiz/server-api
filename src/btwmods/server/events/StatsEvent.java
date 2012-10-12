package btwmods.server.events;
import java.util.EventObject;

import btwmods.ServerAPI.StatsProcessor;
import net.minecraft.server.MinecraftServer;

public class StatsEvent extends EventObject {
	
	private StatsProcessor processor;
	private long[] tickTimeArray = null;
	private long[] sentPacketCountArray = null;
	private long[] sentPacketSizeArray = null;
	private long[] receivedPacketCountArray = null;
	private long[] receivedPacketSizeArray = null;
	private long[][] worldTickTimeArray = null;
	
	public final int tickCounter;
	public final double tickTimeAverage;
	public final double sentPacketCountAverage;
	public final double sentPacketSizeAverage;
	public final double receivedPacketCountAverage;
	public final double receivedPacketSizeAverage;

	public long[] getTickTimeArray() {
		if (tickTimeArray == null)
			tickTimeArray = processor.tickTimeArray.clone();
			
		return tickTimeArray;
	}
	
	public long[] getSentPacketCountArray() {
		if (sentPacketCountArray == null)
			sentPacketCountArray = processor.sentPacketCountArray.clone();
			
		return sentPacketCountArray;
	}
	
	public long[] getSentPacketSizeArray() {
		if (sentPacketSizeArray == null)
			sentPacketSizeArray = processor.sentPacketSizeArray.clone();
			
		return sentPacketSizeArray;
	}
	
	public long[] getReceivedPacketCountArray() {
		if (receivedPacketCountArray == null)
			receivedPacketCountArray = processor.receivedPacketCountArray.clone();
			
		return receivedPacketCountArray;
	}
	
	public long[] getReceivedPacketSizeArray() {
		if (receivedPacketSizeArray == null)
			receivedPacketSizeArray = processor.receivedPacketSizeArray.clone();
			
		return receivedPacketSizeArray;
	}
	
	
	public long[][] getWorldTickTimeArray() {
		if (worldTickTimeArray == null) {
			worldTickTimeArray = new long[processor.worldTickTimeArray.length][];
			for (int i = 0; i < worldTickTimeArray.length; i++) {
				worldTickTimeArray[i] = processor.worldTickTimeArray[i].clone();
			}
		}
		return worldTickTimeArray;
	}
	
	public StatsEvent(MinecraftServer server, StatsProcessor processor) {
		super(server);
		this.processor = processor;
		
		tickCounter = processor.tickCounter;
		tickTimeAverage = (double)processor.tickTimeTotal / (double)Math.min(100, tickCounter);
		sentPacketCountAverage = (double)processor.sentPacketCountTotal / (double)Math.min(100, tickCounter);
		sentPacketSizeAverage = (double)processor.sentPacketSizeTotal / (double)Math.min(100, tickCounter);
		receivedPacketCountAverage = (double)processor.receivedPacketCountTotal / (double)Math.min(100, tickCounter);
		receivedPacketSizeAverage = (double)processor.receivedPacketSizeTotal / (double)Math.min(100, tickCounter);
	}
}
