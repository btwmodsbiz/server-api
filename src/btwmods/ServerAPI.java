package btwmods;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.server.MinecraftServer;
import btwmods.EventDispatcher;
import btwmods.server.Measurements;
import btwmods.server.Tick;
import btwmods.server.events.StatsEvent;
import btwmods.server.listeners.IStatsListener;

public class ServerAPI {

	public static Measurements measurements = new Measurements<Tick>();
	
	private static volatile StatsProcessor statsProcessor = null;
	
	private static EventDispatcher listeners = EventDispatcherFactory.create(new Class[] { IStatsListener.class });
	private static ConcurrentLinkedQueue<QueuedTickStats> statsQueue = new ConcurrentLinkedQueue<QueuedTickStats>();
	
	private ServerAPI() {}

	public static void addListener(IAPIListener listener) {
		if (listener instanceof IStatsListener) {
			listeners.queuedAddListener(listener, IStatsListener.class);

			if (statsProcessor == null) {
				Thread thread = new Thread(statsProcessor = new StatsProcessor());
				thread.setName("ServerAPI StatsListeners");
				thread.start();
			}
		}
		else {
			listeners.addListener(listener);
		}
	}

	public static void removeListener(IAPIListener listener) {
		if (listener instanceof IStatsListener) {
			listeners.queuedRemoveListener(listener, IStatsListener.class);
		}
		else {
			listeners.removeListener(listener);
		}
	}

	public static void startTick(MinecraftServer server, int tickCounter) {
		// Process any failures that may be queued from the last tick.
		ModLoader.processFailureQueue();
	}

	public static void endTick(MinecraftServer server, int tickCounter) {
		QueuedTickStats stats = new QueuedTickStats();
		
		stats.tickCounter = tickCounter;
		stats.tickTime = server.tickTimeArray[tickCounter % 100];
		stats.sentPacketCount = server.sentPacketCountArray[tickCounter % 100];
		stats.sentPacketSize = server.sentPacketSizeArray[tickCounter % 100];
		stats.receivedPacketCount = server.receivedPacketCountArray[tickCounter % 100];
		stats.receivedPacketSize = server.receivedPacketSizeArray[tickCounter % 100];
		
		stats.worldTickTimes = new long[server.timeOfLastDimensionTick.length];
		for (int i = 0; i < stats.worldTickTimes.length; i++) {
			stats.worldTickTimes[i] = server.timeOfLastDimensionTick[i][tickCounter % 100];
		}
		
		// Save measurements and clear it for the next round.
		stats.measurements = measurements.startNew();
		
		statsQueue.add(stats);
	}
	
	private static class QueuedTickStats {
		public int tickCounter;
		public long tickTime;
		public long sentPacketCount;
		public long sentPacketSize;
		public long receivedPacketCount;
		public long receivedPacketSize;
		public long[] worldTickTimes;
		ArrayDeque<Tick> measurements;
	}

	public static class StatsProcessor implements Runnable {
		public int tickCounter;
		
		public long[] tickTimeArray = new long[100];
		public long[] sentPacketCountArray = new long[100];
		public long[] sentPacketSizeArray = new long[100];
		public long[] receivedPacketCountArray = new long[100];
		public long[] receivedPacketSizeArray = new long[100];
		
		public long[][] worldTickTimeArray;
		public long[][] mobSpawningArray;
		public long[][] tickUpdateArray;

		public long tickTimeTotal;
		public long sentPacketCountTotal;
		public long sentPacketSizeTotal;
		public long receivedPacketCountTotal;
		public long receivedPacketSizeTotal;
		
		public long[] worldTickTimeTotals;
		public long[] mobSpawningTotals;
		public long[] tickUpdateTotals;
		
		public StatsProcessor() {
			worldTickTimeTotals = new long[MinecraftServer.getServer().worldServers.length];
			worldTickTimeArray = new long[worldTickTimeTotals.length][100];

			mobSpawningTotals = new long[worldTickTimeTotals.length];
			mobSpawningArray = new long[worldTickTimeTotals.length][100];

			tickUpdateTotals = new long[worldTickTimeTotals.length];
			tickUpdateArray = new long[worldTickTimeTotals.length][100];
		}

		@Override
		public void run() {
			while (statsProcessor == this) {
				
				// Stop if the thread if there are no listeners.
				if (listeners.isEmpty(IStatsListener.class)) {
					statsProcessor = null;
				}
				else {
					
					// Process all the queued tick stats.
					QueuedTickStats stats;
					while ((stats = statsQueue.poll()) != null) {
						tickCounter = stats.tickCounter;
						
						// Remove the stats we are replacing from the totals, if we have looped through the last 100 arrays at least once.
						if (tickCounter >= 100) {
							tickTimeTotal -= tickTimeArray[tickCounter % 100];
							sentPacketCountTotal -= sentPacketCountArray[tickCounter % 100];
							sentPacketSizeTotal -= sentPacketSizeArray[tickCounter % 100];
							receivedPacketCountTotal -= receivedPacketCountArray[tickCounter % 100];
							receivedPacketSizeTotal -= receivedPacketSizeArray[tickCounter % 100];
							for (int i = 0; i < worldTickTimeTotals.length; i++) {
								worldTickTimeTotals[i] -= worldTickTimeArray[i][tickCounter % 100];
								mobSpawningTotals[i] -= mobSpawningArray[i][tickCounter % 100];
								tickUpdateTotals[i] -= tickUpdateArray[i][tickCounter % 100];
							}
						}
						
						// Increment the total with the new stats, and also add them to the last 100 arrays.
						tickTimeTotal += tickTimeArray[tickCounter % 100] = stats.tickTime;
						sentPacketCountTotal += sentPacketCountArray[tickCounter % 100] = stats.sentPacketCount;
						sentPacketSizeTotal += sentPacketSizeArray[tickCounter % 100] = stats.sentPacketSize;
						receivedPacketCountTotal += receivedPacketCountArray[tickCounter % 100] = stats.receivedPacketCount;
						receivedPacketSizeTotal += receivedPacketSizeArray[tickCounter % 100] = stats.receivedPacketSize;
						for (int i = 0; i < worldTickTimeTotals.length; i++) {
							worldTickTimeTotals[i] += worldTickTimeArray[i][tickCounter % 100] = stats.worldTickTimes[i];
						}
						
						// Process all the tick measurements.
						Tick tick;
						
						// TODO: Keep track of totals for measurements.

						if (tickCounter >= 100) {
							// TODO: Remove previous values from the totals for the array index that is being reset below.
						}
						
						// Reset the measurement entries to 0
						for (int i = 0; i < worldTickTimeTotals.length; i++) {
							mobSpawningArray[i][tickCounter % 100] = 0;
							tickUpdateArray[i][tickCounter % 100] = 0;
						}
						
						// Add the time taken by each measurement type.
						while ((tick = stats.measurements.poll()) != null) {
							switch (tick.identifier) {
								case MobSpawning:
									mobSpawningArray[tick.dimension][tickCounter % 100] += tick.getTime();
									break;
									
								case tickBlocksAndAmbiance:
									break;
									
								case tickBlocksAndAmbianceSuper:
									break;
									
								case TickUpdate:
									tickUpdateArray[tick.dimension][tickCounter % 100] += tick.getTime();
									break;
									
								default:
									// TODO: display error if not handled?
									break; 
							}
						}
						
						// TODO: process measurements first? (from stats.measurements)
					}
					
					// Run all the listeners.
					StatsEvent event = new StatsEvent(MinecraftServer.getServer(), this);
					((IStatsListener)listeners).statsAction(event);

					try {
						Thread.sleep(50L);
					} catch (InterruptedException e) {
						
					}
				}
			}
		}
	}
}
