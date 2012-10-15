package btwmod.tickmonitor;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.Date;

import btwmods.BasicFormatter;
import btwmods.IMod;
import btwmods.ServerAPI;
import btwmods.io.Settings;
import btwmods.network.CustomPacketEvent;
import btwmods.network.INetworkListener;
import btwmods.server.IStatsListener;
import btwmods.server.StatsEvent;

public class BTWModTickMonitor implements IMod, IStatsListener, INetworkListener {

	private static File htmlFile = new File(new File("."), "stats.html");
    private static final DecimalFormat decimalFormat = new DecimalFormat("########0.000");
	
    private boolean isRunning = true; // TODO: make this false by default.
    private long reportingDelay = 1000;
	private long lastStatsTime;
	private int lastTickCounter = -1;
	
	private int[] ticksPerSecondArray = new int[100];
	
	@Override
	public String getName() {
		return "Tick Monitor";
	}

	@Override
	public void init(Settings settings) {
		lastStatsTime = System.currentTimeMillis();
		
		// Load settings
		if (settings.hasKey("htmlfile") && !(new File(settings.get("htmlfile")).isDirectory())) {
			htmlFile = new File(settings.get("htmlfile"));
		}
		if (settings.isBoolean("runonstartup")) {
			isRunning = settings.getBoolean(settings.get("runonstartup"));
		}
		if (settings.isInt("reportingdelay")) {
			reportingDelay = Math.max(50, settings.getInt(settings.get("reportingdelay")));
		}
		
		// Add the listener only if isRunning is true by default.
		if (isRunning)
			ServerAPI.addListener(this);
	}

	@Override
	public void unload() {
		ServerAPI.removeListener(this);
	}

	@Override
	public void statsAction(StatsEvent event) {
		long currentTime = System.currentTimeMillis();
		
		if (currentTime - lastStatsTime > reportingDelay) {
			
			long timeElapsed = System.currentTimeMillis() - lastStatsTime;
			int numTicks = event.tickCounter - lastTickCounter;
			
			// Debugging loop to ramp up CPU usage by the thread.
			//for (int i = 0; i < 20000; i++) new String(new char[10000]).replace('\0', 'a');
			
			StringBuilder html = new StringBuilder("<html><head><title>Minecraft Server Stats</title><meta http-equiv=\"refresh\" content=\"2\"></head><body><h1>Minecraft Server Stats</h1><table border=\"0\"><tbody>"); 
			
			html.append("<tr><th align=\"right\">Updated:<th><td>").append(BasicFormatter.dateFormat.format(new Date())).append("</td></tr>");
			
			html.append("<tr><td colspan=\"2\" style=\"height: 16px\"></td></tr>");
			
			html.append("<tr><th align=\"right\">Tick Num:<th><td>").append(event.tickCounter);
			if (lastTickCounter >= 0) html.append(" (~").append(decimalFormat.format((double)numTicks / (double)timeElapsed * 1000D)).append("/sec)");
			html.append("</td></tr>");
			
			html.append("<tr><th align=\"right\">Average Full Tick:<th><td>").append(decimalFormat.format(event.serverStats.tickTime.getAverage() * 1.0E-6D)).append(" ms</td></tr>");
			
			double worldsTotal = 0;
			for (int i = 0; i < event.worldStats.length; i++) {
				
				worldsTotal += event.worldStats[i].worldTickTime.getAverage();
				
				double checkedTotal = 
						event.worldStats[i].mobSpawning.getAverage()
						+ event.worldStats[i].blockTick.getAverage()
						+ event.worldStats[i].tickBlocksAndAmbiance.getAverage()
						+ event.worldStats[i].tickBlocksAndAmbianceSuper.getAverage()
						+ event.worldStats[i].entities.getAverage()
						+ event.worldStats[i].timeSync.getAverage();
				
				html.append("<tr><th align=\"right\">Average World ").append(i)
				.append(" Tick:<th><td>").append(decimalFormat.format(event.worldStats[i].worldTickTime.getAverage() * 1.0E-6D) + "ms (" + decimalFormat.format(checkedTotal * 1.0E-6D) + " ms == ")
				.append("E: ").append(decimalFormat.format(event.worldStats[i].entities.getAverage() * 1.0E-6D)).append("ms")
				.append("+ M: ").append(decimalFormat.format(event.worldStats[i].mobSpawning.getAverage() * 1.0E-6D)).append("ms")
				.append(" + B: ").append(decimalFormat.format(event.worldStats[i].blockTick.getAverage() * 1.0E-6D)).append("ms")
				.append(" + A: ").append(decimalFormat.format(event.worldStats[i].tickBlocksAndAmbiance.getAverage() * 1.0E-6D)).append("ms")
				.append(" + AS: ").append(decimalFormat.format(event.worldStats[i].tickBlocksAndAmbianceSuper.getAverage() * 1.0E-6D)).append("ms")
				.append(" + T: ").append(decimalFormat.format(event.worldStats[i].timeSync.getAverage() * 1.0E-6D)).append("ms")
				.append(")</td></tr>");
			}

			html.append("<tr><th align=\"right\">Worlds Total:<th><td>").append(decimalFormat.format(worldsTotal * 1.0E-6D)).append(" ms</td></tr>");
			
			html.append("<tr><td colspan=\"2\" style=\"height: 16px\"></td></tr>");
			
			html.append("<tr><th align=\"right\">Average Received Packet Count:<th><td>").append(decimalFormat.format(event.serverStats.receivedPacketCount.getAverage())).append("</td></tr>");
			html.append("<tr><th align=\"right\">Average Sent Packet Count:<th><td>").append(decimalFormat.format(event.serverStats.sentPacketCount.getAverage())).append("</td></tr>");
			
			html.append("<tr><td colspan=\"2\" style=\"height: 16px\"></td></tr>");

			html.append("<tr><th align=\"right\">Average Received Packet Size:<th><td>").append((int)event.serverStats.receivedPacketSize.getAverage()).append(" bytes</td></tr>");
			html.append("<tr><th align=\"right\">Average Sent Packet Size:<th><td>").append((int)event.serverStats.sentPacketSize.getAverage()).append(" bytes</td></tr>");
			
			html.append("</tbody></table></body></html>");
			
			try {
				FileWriter writer = new FileWriter(htmlFile);
				writer.write(html.toString());
				writer.close();
			}
			catch (Throwable e) {
				net.minecraft.server.MinecraftServer.logger.warning("Tick Monitor failed to write to " + htmlFile.getPath() + ": " + e.getMessage());
			}
			
			if (System.currentTimeMillis() - currentTime > 500)
				net.minecraft.server.MinecraftServer.logger.warning("Tick Monitor took " + (System.currentTimeMillis() - currentTime) + "ms to process stats. Note: This will *not* slow the main Minecraft server thread.");

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
}
