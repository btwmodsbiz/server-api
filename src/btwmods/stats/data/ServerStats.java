package btwmods.stats.data;

import btwmods.measure.Average;

public class ServerStats {
	public long lastTickEnd = -1;
	public final Average tickTime = new Average();
	public String[] players;
	public final Average sentPacketCount = new Average();
	public final Average sentPacketSize = new Average();
	public final Average receivedPacketCount = new Average();
	public final Average receivedPacketSize = new Average();
	public final Average statsThreadTime = new Average();
	public final Average statsThreadQueueCount = new Average();
	public long sentByes = 0L;
	public long receivedByes = 0L;
}