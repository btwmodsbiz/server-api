package btwmod.tickmonitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

import btwmods.BasicFormatter;
import btwmods.IMod;
import btwmods.NetworkAPI;
import btwmods.ServerAPI;
import btwmods.network.CustomPacketEvent;
import btwmods.network.INetworkListener;
import btwmods.server.events.StatsEvent;
import btwmods.server.listeners.IStatsListener;

public class BTWModTickMonitor implements IMod, IStatsListener, INetworkListener {

	private static final File htmlFile = new File(new File("."), "stats.html");
    private static final DecimalFormat decimalFormat = new DecimalFormat("########0.000");
	private long reportingDelay = 1000; //TODO: replace with 250
	private long lastStatsTime = System.currentTimeMillis();
	
	private int[] ticksPerSecondArray = new int[100];
	
	@Override
	public String getName() {
		return "Tick Monitor";
	}

	@Override
	public void init() {
		ServerAPI.addListener(this);
		if (NetworkAPI.registerCustomChannel("TCK", this)) {
			
		}
	}

	@Override
	public void unload() {
		ServerAPI.removeListener(this);
		NetworkAPI.unregisterCustomChannel("TCK", this);
	}

	@Override
	public void statsAction(StatsEvent event) {
		if (System.currentTimeMillis() - lastStatsTime > reportingDelay) {
			lastStatsTime = System.currentTimeMillis();
			
			StringBuilder html = new StringBuilder("<html><head><title>Minecraft Server Stats</title><meta http-equiv=\"refresh\" content=\"2\"></head><body><h1>Minecraft Server Stats</h1><table border=\"0\"><tbody>"); 
			
			html.append("<tr><th align=\"right\">Updated:<th><td>" + BasicFormatter.dateFormat.format(new Date()) + "</td>");
			
			html.append("<tr><th align=\"right\">Tick Num:<th><td>" + event.tickCounter + "</td>");
			html.append("<tr><th align=\"right\">Average Tick Time:<th><td>" + decimalFormat.format(event.averageTickTime * 1.0E-6D) + " ms</td>");
			
			html.append("<tr><th align=\"right\">Average Received Packet Size:<th><td>" + (int)event.averageReceivedPacketSize + " bytes</td>");
			html.append("<tr><th align=\"right\">Average Received Packet Count:<th><td>" + (int)event.averageReceivedPacketCount + "</td>");
			
			html.append("<tr><th align=\"right\">Average Sent Packet Size:<th><td>" + (int)event.averageSentPacketSize + " bytes</td>");
			html.append("<tr><th align=\"right\">Average Sent Packet Count:<th><td>" + (int)event.averageSentPacketCount + "</td>");
			
			for (int i = 0; i < event.averageWorldTickTime.length; i++) {
				html.append("<tr><th align=\"right\">Average World " + i + " Tick Time:<th><td>" + decimalFormat.format(event.averageWorldTickTime[i] * 1.0E-6D) + " ms</td>");
			}
			
			html.append("</tbody></table></body></html>");
			
			try {
				FileWriter writer = new FileWriter(htmlFile);
				writer.write(html.toString());
				writer.close();
			}
			catch (IOException e) {
				net.minecraft.server.MinecraftServer.logger.warning("Failed to write to " + htmlFile.getPath() + ": " + e.getMessage());
			}
			//net.minecraft.server.MinecraftServer.logger.info("Stats: " + decimalFormat.format(event.averageTickTime * 1.0E-6D) + "ms tick");
			//net.minecraft.server.MinecraftServer.logger.info("Stats: " + (int)(event.averageReceivedPacketSize) + " average bytes received");
			//net.minecraft.server.MinecraftServer.logger.info("Stats: " + (int)(event.averageReceivedPacketCount) + " average received");
			//net.minecraft.server.MinecraftServer.logger.info("Lvl 0 tick: " + decimalFormat.format(event.averageWorldTickTime[0] * 1.0E-6D) + " ms");
			
			//if (MinecraftServer.getServer().worldServers != null) {
	        //    for (int var3 = 0; var3 < MinecraftServer.getServer().worldServers.length; ++var3) {
	            	
	                //if (MinecraftServer.getServer().worldServers[var3] != null && MinecraftServer.getServer().worldServers[var3].theChunkProviderServer != null)
	                //{
	                //    this.displayStrings[5 + var3] = this.displayStrings[5 + var3] + ", " + MinecraftServer.getServer().worldServers[var3].theChunkProviderServer.makeString();
	                //}
	        //    }
	        //}
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
