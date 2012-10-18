package btwmods.stats;
import java.util.EventObject;

import btwmods.stats.data.ServerStats;
import btwmods.stats.data.WorldStats;
import net.minecraft.server.MinecraftServer;

public class StatsEvent extends EventObject {
	
	public final int tickCounter;
	public final ServerStats serverStats;
	public final WorldStats[] worldStats;

	public StatsEvent(MinecraftServer server, int tickCounter, ServerStats serverStats, WorldStats[] worldStats) {
		super(server);
		this.tickCounter = tickCounter;
		this.serverStats = serverStats;
		this.worldStats = worldStats;
	}
}
