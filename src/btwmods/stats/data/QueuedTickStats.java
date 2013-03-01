package btwmods.stats.data;

import java.util.ArrayDeque;

import btwmods.measure.Measurement;

public class QueuedTickStats {
	public String statProfile;
	public long tickEnd;
	public int tickCounter;
	public long tickTime;
	public String[] players;
	public long sentPacketCount;
	public long sentPacketSize;
	public long receivedPacketCount;
	public long receivedPacketSize;
	public ArrayDeque<Measurement> measurements;
	public long bytesSent;
	public long bytesReceived;
	public long handlerInvocations;
}