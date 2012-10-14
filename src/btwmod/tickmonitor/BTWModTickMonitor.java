package btwmod.tickmonitor;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.Date;

import btwmods.BasicFormatter;
import btwmods.IMod;
import btwmods.ServerAPI;
import btwmods.network.CustomPacketEvent;
import btwmods.network.INetworkListener;
import btwmods.server.events.StatsEvent;
import btwmods.server.listeners.IStatsListener;

public class BTWModTickMonitor implements IMod, IStatsListener, INetworkListener {

	private static final File htmlFile = new File(new File("."), "stats.html");
    private static final DecimalFormat decimalFormat = new DecimalFormat("########0.000");
	
    private boolean isRunning = true;
    private long reportingDelay = 1000;
	private long lastStatsTime;
	private int lastTickCounter = -1;
	
	private int[] ticksPerSecondArray = new int[100];
	
	@Override
	public String getName() {
		return "Tick Monitor";
	}

	@Override
	public void init() {
		lastStatsTime = System.currentTimeMillis();
		
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
			
			html.append("<tr><th align=\"right\">Updated:<th><td>").append(BasicFormatter.dateFormat.format(new Date())).append("</td>");
			
			html.append("<tr><th align=\"right\">Tick Num:<th><td>").append(event.tickCounter);
			if (lastTickCounter >= 0) html.append(" (~").append(decimalFormat.format((double)numTicks / (double)timeElapsed * 1000D)).append("/sec)");
			html.append("</td>");
			
			html.append("<tr><th align=\"right\">Average Tick Time:<th><td>").append(decimalFormat.format(event.averageTickTime * 1.0E-6D)).append(" ms</td>");
			
			html.append("<tr><th align=\"right\">Average Received Packet Size:<th><td>").append((int)event.averageReceivedPacketSize).append(" bytes</td>");
			html.append("<tr><th align=\"right\">Average Received Packet Count:<th><td>").append((int)event.averageReceivedPacketCount).append("</td>");
			
			html.append("<tr><th align=\"right\">Average Sent Packet Size:<th><td>").append((int)event.averageSentPacketSize).append(" bytes</td>");
			html.append("<tr><th align=\"right\">Average Sent Packet Count:<th><td>").append((int)event.averageSentPacketCount).append("</td>");
			
			for (int i = 0; i < event.averageWorldTickTime.length; i++) {
				html.append("<tr><th align=\"right\">Average World ").append(i).append(" Tick Time:<th><td>").append(decimalFormat.format(event.averageWorldTickTime[i] * 1.0E-6D)).append(" ms</td>");
			}
			
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
