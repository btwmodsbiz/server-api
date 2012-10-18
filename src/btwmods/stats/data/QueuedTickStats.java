package btwmods.stats.data;

import java.util.ArrayDeque;

import btwmods.stats.measurements.TickMeasurement;

public class QueuedTickStats {
	public int tickCounter;
	public long tickTime;
	public long sentPacketCount;
	public long sentPacketSize;
	public long receivedPacketCount;
	public long receivedPacketSize;
	public long[] worldTickTimes;
	public ArrayDeque<TickMeasurement> measurements;
}