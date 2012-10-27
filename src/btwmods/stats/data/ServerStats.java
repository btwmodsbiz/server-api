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
	public long bytesSent = 0L;
	public long bytesReceived = 0L;
	public long bytesSentToPlayers = 0L;
	public long bytesReceivedFromPlayers = 0L;
}