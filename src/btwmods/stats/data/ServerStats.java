package btwmods.stats.data;

import java.util.EnumMap;
import java.util.Map;

import btwmods.Stat;
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
	public long handlerInovcations = 0L;
	
	public final EnumMap<Stat, Map<Class, Average>> timeByListener = new EnumMap<Stat, Map<Class, Average>>(Stat.class);
	public final EnumMap<Stat, Map<Class, Average>> callsByListener = new EnumMap<Stat, Map<Class, Average>>(Stat.class);
	
	public final EnumMap<Stat, Map<Class, Average>> timeByMod = new EnumMap<Stat, Map<Class, Average>>(Stat.class);
}