package btwmod.tickmonitor;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.CommandHandler;

import btwmods.IMod;
import btwmods.StatsAPI;
import btwmods.StatsAPI.StatsProcessor.ChunkStats;
import btwmods.StatsAPI.StatsProcessor.EntityStats;
import btwmods.io.Settings;
import btwmods.measure.Average;
import btwmods.network.CustomPacketEvent;
import btwmods.network.INetworkListener;
import btwmods.player.IInstanceListener;
import btwmods.player.InstanceEvent;
import btwmods.stats.ChunkStatsComparator;
import btwmods.stats.EntityStatsComparator;
import btwmods.stats.IStatsListener;
import btwmods.stats.StatsEvent;
import btwmods.stats.ChunkStatsComparator.Stat;
import btwmods.util.BasicFormatter;

public class BTWModTickMonitor implements IMod, IStatsListener, INetworkListener, IInstanceListener {

	private static int topNumber = 20;
	private static String publicLink = null;
	private static File htmlFile = new File(new File("."), "stats.html");
    private static final DecimalFormat decimalFormat = new DecimalFormat("########0.000");
	
    private boolean isRunning = true; // TODO: make this false by default.
    private long tooLongWarningTime = 1000;
    private long reportingDelay = 1000;
	private long lastStatsTime = 0;
	private int lastTickCounter = -1;
	private Average statsActionTime = new Average(10);
	private Average statsActionIOTime = new Average(10);
	
	private int[] ticksPerSecondArray = new int[100];
	
	@Override
	public String getName() {
		return "Tick Monitor";
	}

	@Override
	public void init(Settings settings) {
		lastStatsTime = System.currentTimeMillis();
		
		// Load settings
		if (settings.hasKey("publiclink") && !(new File(settings.get("publiclink")).isDirectory())) {
			publicLink = settings.get("publiclink");
		}
		if (settings.hasKey("htmlfile") && !(new File(settings.get("htmlfile")).isDirectory())) {
			htmlFile = new File(settings.get("htmlfile"));
		}
		if (settings.isBoolean("runonstartup")) {
			isRunning = settings.getBoolean("runonstartup");
		}
		if (settings.isInt("reportingdelay")) {
			reportingDelay = Math.max(50, settings.getInt("reportingdelay"));
		}
		if (settings.isLong("toolongwarningtime")) {
			tooLongWarningTime = Math.min(500L, settings.getLong("toolongwarningtime"));
		}
		
		// Add the listener only if isRunning is true by default.
		if (isRunning)
			StatsAPI.addListener(this);
		
		((CommandHandler)MinecraftServer.getServer().getCommandManager()).registerCommand(new MonitorCommand(this));
	}

	@Override
	public void unload() {
		StatsAPI.removeListener(this);
	}
	
	public void setIsRunning(boolean value) {
		if (isRunning == value)
			return;
		
		lastStatsTime = 0;
		lastTickCounter = -1;
		
		if (isRunning)
			StatsAPI.removeListener(this);
		else
			StatsAPI.addListener(this);
		
		isRunning = value;
	}

	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * WARNING: This runs in a separate thread. Be very strict about what this accesses beyond the passed parameter. Do
	 * not access any of the APIs, and be careful how class variables are used outside of statsAction().
	 */
	@Override
	public void statsAction(StatsEvent event) {
		long currentTime = System.currentTimeMillis();
		long startNano = System.nanoTime();
		boolean detailedMeasurementsEnabled = StatsAPI.detailedMeasurementsEnabled;
		
		if (lastStatsTime == 0) {
			lastStatsTime = System.currentTimeMillis();
		}
		
		else if (currentTime - lastStatsTime > reportingDelay) {
			
			long timeElapsed = System.currentTimeMillis() - lastStatsTime;
			int numTicks = event.tickCounter - lastTickCounter;
			
			// Debugging loop to ramp up CPU usage by the thread.
			//for (int i = 0; i < 20000; i++) new String(new char[10000]).replace('\0', 'a');
			
			StringBuilder html = new StringBuilder("<html><head><title>Minecraft Server Stats</title><meta http-equiv=\"refresh\" content=\"2\"></head><body><h1>Minecraft Server Stats</h1><table border=\"0\"><tbody>"); 
			
			html.append("<tr><th align=\"right\">Updated:<th><td>").append(BasicFormatter.dateFormat.format(new Date())).append("</td></tr>");
			html.append("<tr><th align=\"right\">Average StatsAPI Thread Time:<th><td>").append(decimalFormat.format(event.serverStats.statsThreadTime.getAverage() * 1.0E-6D)).append(" ms</td></tr>");
			html.append("<tr><th align=\"right\">Average StatsAPI Polled:<th><td>").append(decimalFormat.format(event.serverStats.statsThreadQueueCount.getAverage())).append("</td></tr>");
			
			html.append("<tr><th align=\"right\">Average Tick Monitor Time:<th><td>");
			if (statsActionTime.getTick() == 0)
				html.append("...");
			else
				html.append(decimalFormat.format(statsActionTime.getAverage() * 1.0E-6D)).append("ms (" + (int)(statsActionIOTime.getAverage() * 100 / statsActionTime.getAverage()) + "% IO)");
			html.append("</td></tr>");
			
			html.append("<tr><td colspan=\"2\" style=\"height: 16px\"></td></tr>");
			
			html.append("<tr><th align=\"right\">Tick Num:<th><td>").append(event.tickCounter);
			if (lastTickCounter >= 0) html.append(" (~").append(decimalFormat.format((double)numTicks / (double)timeElapsed * 1000D)).append("/sec)");
			html.append("</td></tr>");
			
			html.append("<tr><th align=\"right\">Average Full Tick:<th><td>").append(decimalFormat.format(event.serverStats.tickTime.getAverage() * 1.0E-6D)).append(" ms</td></tr>");
			
			html.append("<tr><td colspan=\"2\" style=\"height: 16px\"></td></tr>");
			
			double worldsTotal = 0;
			for (int i = 0; i < event.worldStats.length; i++) {
				
				worldsTotal += event.worldStats[i].worldTickTime.getAverage();
				
				/*double checkedTotal = 
						event.worldStats[i].mobSpawning.getAverage()
						+ event.worldStats[i].blockTick.getAverage()
						+ event.worldStats[i].tickBlocksAndAmbiance.getAverage()
						+ event.worldStats[i].tickBlocksAndAmbianceSuper.getAverage()
						+ event.worldStats[i].entities.getAverage()
						+ event.worldStats[i].timeSync.getAverage();*/
				
				html.append("<tr><th align=\"right\">World ").append(i).append(" Averages:<th><td>")
					.append(decimalFormat.format(event.worldStats[i].worldTickTime.getAverage() * 1.0E-6D) + "ms");
				
				if (detailedMeasurementsEnabled)
					html.append(" (" /*+ decimalFormat.format(checkedTotal * 1.0E-6D) + " ms == "*/)
						.append("E: ").append(decimalFormat.format(event.worldStats[i].entities.getAverage() * 1.0E-6D)).append("ms")
						.append(" + M: ").append(decimalFormat.format(event.worldStats[i].mobSpawning.getAverage() * 1.0E-6D)).append("ms")
						.append(" + B: ").append(decimalFormat.format(event.worldStats[i].blockTick.getAverage() * 1.0E-6D)).append("ms")
						.append(" + A: ").append(decimalFormat.format(event.worldStats[i].tickBlocksAndAmbiance.getAverage() * 1.0E-6D)).append("ms")
						.append(" + AS: ").append(decimalFormat.format(event.worldStats[i].tickBlocksAndAmbianceSuper.getAverage() * 1.0E-6D)).append("ms")
						.append(" + T: ").append(decimalFormat.format(event.worldStats[i].timeSync.getAverage() * 1.0E-6D)).append("ms")
						.append(" + CS: ").append(decimalFormat.format(event.worldStats[i].buildActiveChunkSet.getAverage() * 1.0E-6D)).append("ms")
						.append(" + L: ").append(decimalFormat.format(event.worldStats[i].checkPlayerLight.getAverage() * 1.0E-6D)).append("ms")
						.append(")");
				
				html.append("</td></tr>");
				
				if (detailedMeasurementsEnabled)
					html.append("<tr><th align=\"right\">&nbsp;<th><td>")
						.append((int)event.worldStats[i].measurementQueue.getAverage()).append(" measurements per tick")
						.append("</td></tr>");
			}

			html.append("<tr><th align=\"right\">Worlds Total:<th><td>").append(decimalFormat.format(worldsTotal * 1.0E-6D)).append(" ms (" + (int)(worldsTotal / event.serverStats.tickTime.getAverage() * 100) + "% of full tick)</td></tr>");
			
			html.append("<tr><td colspan=\"2\" style=\"height: 16px\"></td></tr>");
			
			html.append("<tr><th align=\"right\">Average Received Packet Count:<th><td>").append(decimalFormat.format(event.serverStats.receivedPacketCount.getAverage())).append("</td></tr>");
			html.append("<tr><th align=\"right\">Average Sent Packet Count:<th><td>").append(decimalFormat.format(event.serverStats.sentPacketCount.getAverage())).append("</td></tr>");
			
			html.append("<tr><td colspan=\"2\" style=\"height: 16px\"></td></tr>");

			html.append("<tr><th align=\"right\">Average Received Packet Size:<th><td>").append((int)event.serverStats.receivedPacketSize.getAverage()).append(" bytes</td></tr>");
			html.append("<tr><th align=\"right\">Average Sent Packet Size:<th><td>").append((int)event.serverStats.sentPacketSize.getAverage()).append(" bytes</td></tr>");
			
			html.append("</tbody></table>");
			
			if (detailedMeasurementsEnabled) {
			
				List<Map.Entry<ChunkCoordIntPair, ChunkStats>> chunkEntries = new ArrayList<Map.Entry<ChunkCoordIntPair, ChunkStats>>(event.worldStats[0].chunkStats.entrySet());
	
				html.append("<h2>Top " + topNumber + ":</h2>");
				
				html.append("<table border=\"0\"><thead><tr><th>Chunks By Tick Time</th><th>Chunks By Entity Count</th><th>Entities By Tick Time</th><th>Entities By Count</th></tr></thead><tbody><tr><td valign=\"top\">");
	
				{
					Collections.sort(chunkEntries, new ChunkStatsComparator<ChunkCoordIntPair>(Stat.TICKTIME, true));
					html.append("<table border=\"0\"><thead><tr><th>Chunk</th><th>Tick Time</th><th>Entities</th></tr></thead><tbody>");
					double chunksTotal = 0;
					int entitiesTotal = 0;
					int displayed = 0;
					for (int i = 0; i < chunkEntries.size(); i++) {
						if (chunkEntries.get(i).getValue().tickTime.getTotal() != 0 && displayed <= topNumber) {
							displayed++;
							html.append("<tr><td>").append(chunkEntries.get(i).getKey().chunkXPos).append("/").append(chunkEntries.get(i).getKey().chunkZPos)
									.append("</td><td>").append(decimalFormat.format(chunkEntries.get(i).getValue().tickTime.getAverage() * 1.0E-6D))
									.append(" ms</td><td>").append(chunkEntries.get(i).getValue().entityCount)
									.append("</td></tr>");
						}
	
						chunksTotal += chunkEntries.get(i).getValue().tickTime.getAverage();
						entitiesTotal += chunkEntries.get(i).getValue().entityCount;
					}
	
					html.append("<tr><td>Totals</td><td>").append(decimalFormat.format(chunksTotal * 1.0E-6D)).append("ms</td><td>").append(entitiesTotal).append("</td></tr>");
					html.append("</tbody></table>");
				}
				
				html.append("</td><td valign=\"top\">");
	
				{
					Collections.sort(chunkEntries, new ChunkStatsComparator<ChunkCoordIntPair>(Stat.ENTITIES, true));
					html.append("<table border=\"0\"><thead><tr><th>Chunk</th><th>Tick Time</th><th>Entities</th></tr></thead><tbody>");
					double chunksTotal = 0;
					int displayed = 0;
					int entitiesTotal = 0;
					for (int i = 0; i < chunkEntries.size(); i++) {
						if (chunkEntries.get(i).getValue().tickTime.getTotal() != 0 && displayed <= topNumber) {
							displayed++;
							html.append("<tr><td>").append(chunkEntries.get(i).getKey().chunkXPos).append("/").append(chunkEntries.get(i).getKey().chunkZPos)
									.append("</td><td>").append(decimalFormat.format(chunkEntries.get(i).getValue().tickTime.getAverage() * 1.0E-6D))
									.append(" ms</td><td>").append(chunkEntries.get(i).getValue().entityCount)
									.append("</td></tr>");
						}
	
						chunksTotal += chunkEntries.get(i).getValue().tickTime.getAverage();
						entitiesTotal += chunkEntries.get(i).getValue().entityCount;
					}
	
					html.append("<tr><td>Totals</td><td>").append(decimalFormat.format(chunksTotal * 1.0E-6D)).append("ms</td><td>").append(entitiesTotal).append("</td></tr>");
					html.append("</tbody></table>");
				}

				List<Map.Entry<Class, EntityStats>> entityEntries = new ArrayList<Map.Entry<Class, EntityStats>>(event.worldStats[0].entityStats.entrySet());
				
				html.append("</td><td valign=\"top\">");
	
				{
					Collections.sort(entityEntries, new EntityStatsComparator<Class>(EntityStatsComparator.Stat.TICKTIME, true));
					html.append("<table border=\"0\"><thead><tr><th>Entity</th><th>Tick Time</th><th>Count</th></tr></thead><tbody>");
					double entitiesTotal = 0;
					int displayed = 0;
					for (int i = 0; i < entityEntries.size(); i++) {
						if (entityEntries.get(i).getValue().tickTime.getTotal() != 0 && displayed <= topNumber) {
							displayed++;
							html.append("<tr><td>").append(entityEntries.get(i).getKey().getSimpleName())
									.append("</td><td>").append(decimalFormat.format(entityEntries.get(i).getValue().tickTime.getAverage() * 1.0E-6D))
									.append(" ms</td><td>").append(entityEntries.get(i).getValue().entityCount)
									.append("</td></tr>");
						}
	
						entitiesTotal += entityEntries.get(i).getValue().tickTime.getAverage();
					}
	
					html.append("<tr><td>Totals</td><td colspan=\"2\">").append(decimalFormat.format(entitiesTotal * 1.0E-6D)).append("ms</td></tr>");
					html.append("</tbody></table>");
				}
				
				html.append("</td><td valign=\"top\">");
	
				{
					Collections.sort(entityEntries, new EntityStatsComparator<Class>(EntityStatsComparator.Stat.ENTITIES, true));
					html.append("<table border=\"0\"><thead><tr><th>Entity</th><th>Tick Time</th><th>Count</th></tr></thead><tbody>");
					double entitiesTotal = 0;
					int displayed = 0;
					for (int i = 0; i < entityEntries.size(); i++) {
						if (entityEntries.get(i).getValue().tickTime.getTotal() != 0 && displayed <= topNumber) {
							displayed++;
							html.append("<tr><td>").append(entityEntries.get(i).getKey().getSimpleName())
									.append("</td><td>").append(decimalFormat.format(entityEntries.get(i).getValue().tickTime.getAverage() * 1.0E-6D))
									.append(" ms</td><td>").append(entityEntries.get(i).getValue().entityCount)
									.append("</td></tr>");
						}
	
						entitiesTotal += entityEntries.get(i).getValue().tickTime.getAverage();
					}
	
					html.append("<tr><td>Totals</td><td colspan=\"2\">").append(decimalFormat.format(entitiesTotal * 1.0E-6D)).append("ms</td></tr>");
					html.append("</tbody></table>");
				}
	
				html.append("</td></tr></tbody></table>");
	
				html.append("</body></html>");
			
			}

			long startWriteTime = System.currentTimeMillis();
			long startWriteNano = System.nanoTime();
			
			try {
				FileWriter writer = new FileWriter(htmlFile);
				writer.write(html.toString());
				writer.close();
			}
			catch (Throwable e) {
				net.minecraft.server.MinecraftServer.logger.warning("Tick Monitor failed to write to " + htmlFile.getPath() + ": " + e.getMessage());
			}
			
			long endNano = System.nanoTime();
			long endTime = System.currentTimeMillis();
			
			if (System.currentTimeMillis() - currentTime > tooLongWarningTime)
				net.minecraft.server.MinecraftServer.logger.warning("Tick Monitor took " + (endTime - currentTime) + "ms (~" + ((endTime - startWriteTime) * 100 / (endTime - currentTime)) + "% disk IO) to process stats. Note: This will *not* slow the main Minecraft server thread.");

			statsActionTime.record(endNano - startNano);
			statsActionIOTime.record(endNano - startWriteNano);
			lastTickCounter = event.tickCounter;
			lastStatsTime = System.currentTimeMillis();
		}
	}

	@Override
	public IMod getMod() {
		return this;
	}

	@Override
	public void customPacketAction(CustomPacketEvent event) {
		// TODO: remove debug throw
		throw new IllegalArgumentException();
	}

	@Override
	public void instanceAction(InstanceEvent event) {
		if (publicLink != null && event.getType() == InstanceEvent.TYPE.LOGIN)
			event.getPlayerInstance().sendChatToPlayer("Tick stats are available at " + publicLink);
	}
}
