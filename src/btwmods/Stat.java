package btwmods;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityTrackerEntry;
import net.minecraft.src.NextTickListEntry;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import btwmods.measure.Average;
import btwmods.network.NetworkType;
import btwmods.stats.measurements.StatBlock;
import btwmods.stats.measurements.StatChunk;
import btwmods.stats.measurements.StatNetworkPlayer;
import btwmods.stats.measurements.StatPositionedClass;
import btwmods.stats.measurements.StatWorld;
import btwmods.stats.measurements.StatWorldValue;

public enum Stat {
	WORLD_TICK(1.0E-6D),
	WORLD_LOADED_CHUNKS,
	WORLD_CACHED_CHUNKS,
	WORLD_DROPPED_CHUNKS,
	WORLD_LOADED_ENTITIES,
	WORLD_LOADED_TILE_ENTITIES,
	WORLD_TRACKED_ENTITIES,
	
	MOB_SPAWNING(1.0E-6D),
	BLOCK_UPDATE(1.0E-6D),
	BUILD_ACTIVE_CHUNKSET(1.0E-6D),
	CHECK_PLAYER_LIGHT(1.0E-6D),
	TIME_SYNC(1.0E-6D),
	
	MOOD_LIGHT_AND_WEATHER(1.0E-6D),
	LIGHTNING_AND_RAIN(1.0E-6D),

	ENTITIES_SECTION(1.0E-6D),
		WEATHER_EFFECTS(1.0E-6D),
		ENTITIES_REMOVE(1.0E-6D),
		ENTITIES_REGULAR(1.0E-6D),
			ENTITY_UPDATE(1.0E-6D),
		ENTITIES_TILE(1.0E-6D),
			TILE_ENTITY_UPDATE(1.0E-6D),
		ENTITIES_TILEPENDING(1.0E-6D),
		
		UPDATE_TRACKED_ENTITY_PLAYER_LISTS(1.0E-6D),
		UPDATE_PLAYER_ENTITIES(1.0E-6D),
		UPDATE_TRACKED_ENTITY_PLAYER_LIST(1.0E-6D),
	
	LOAD_CHUNK,
	LOAD_CHUNK_TIME,
	SPAWN_LIVING;
	
	public static long bytesSent = 0;
	public static long bytesReceived = 0;
	
	public boolean enabled = true;
	
	public final double scale;
	public final int averageResolution = Average.RESOLUTION;
	
	private Stat() {
		this(1.0D);
	}
	
	private Stat(double scale) {
		this.scale = scale;
	}
	
	public void begin(World world) {
		StatsAPI.begin(new StatWorld(this, world));
	}
	
	public void end() {
		StatsAPI.end(this);
	}
	
	public static void beginBlockUpdate(World world, Block block, int x, int y, int z) {
		StatsAPI.begin(new StatBlock(Stat.BLOCK_UPDATE, world, block, x, y, z));
	}

	public static void beginBlockUpdate(World world, NextTickListEntry blockUpdate) {
		StatsAPI.begin(new StatBlock(Stat.BLOCK_UPDATE, world, blockUpdate));
	}

	public static void beginEntityUpdate(World world, Entity entity) {
		StatsAPI.begin(new StatPositionedClass(world, entity));
	}

	public static void beginTileEntityUpdate(World world, TileEntity tileEntity) {
		StatsAPI.begin(new StatPositionedClass(Stat.TILE_ENTITY_UPDATE, world, tileEntity));
	}

	public static void beginUpdateTrackedEntityPlayerList(World world, EntityTrackerEntry trackerEntry) {
		StatsAPI.begin(new StatPositionedClass(world, trackerEntry));
	}

	public static void beginLoadChunk(World world, int chunkX, int chunkY) {
		StatsAPI.begin(new StatChunk(Stat.LOAD_CHUNK, world, chunkX, chunkY));
	}

	public static void recordSpawning(World world, int spawned) {
		StatsAPI.record(new StatWorldValue(Stat.SPAWN_LIVING, world, spawned));
	}
	
	public static void recordNetworkIO(NetworkType type, int bytes) {
		if (type == NetworkType.RECEIVED)
			bytesReceived += (long)bytes;
		else
			bytesSent += (long)bytes;
	}
	
	public static void recordNetworkIO(NetworkType type, int bytes, String username) {
		recordNetworkIO(type, bytes);
		StatsAPI.record(new StatNetworkPlayer(type, username, bytes));
	}
	
	public static void recordNetworkIO(NetworkType type, int bytes, EntityPlayer player) {
		recordNetworkIO(type, bytes);
		
		if (player != null)
			StatsAPI.record(new StatNetworkPlayer(type, player.username, bytes));
	}
	
	public String nameAsCamelCase() {
		String[] parts = super.toString().split("_");
		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			if (sb.length() == 0)
				sb.append(part.toLowerCase());
			else
				sb.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase());
		}
		return sb.toString();
	}
}
