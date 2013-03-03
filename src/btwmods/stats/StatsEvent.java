package btwmods.stats;

import btwmods.events.APIEvent;
import btwmods.stats.data.ServerStats;
import btwmods.stats.data.WorldStats;
import net.minecraft.server.MinecraftServer;

public class StatsEvent extends APIEvent {
	
	public final int tickCounter;
	public final String statsProfile;
	public final ServerStats serverStats;
	public final WorldStats[] worldStats;

	public StatsEvent(MinecraftServer server, int tickCounter, String statsProfile, ServerStats serverStats, WorldStats[] worldStats) {
		super(server);
		this.tickCounter = tickCounter;
		this.statsProfile = statsProfile;
		this.serverStats = serverStats;
		this.worldStats = worldStats;
	}
}
