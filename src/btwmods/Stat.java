package btwmods;

import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicLong;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityTrackerEntry;
import net.minecraft.src.NextTickListEntry;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import btwmods.measure.Average;
import btwmods.network.NetworkType;
import btwmods.stats.measurements.StatChunk;
import btwmods.stats.measurements.StatPositionedClass;
import btwmods.stats.measurements.StatWorld;
import btwmods.stats.measurements.StatWorldValue;

public enum Stat {
	WORLD_TICK(true, 1.0E-6D),
	WORLD_LOADED_CHUNKS(true),
	WORLD_CACHED_CHUNKS(true),
	WORLD_DROPPED_CHUNKS(true),
	WORLD_LOADED_ENTITIES(true),
	WORLD_LOADED_TILE_ENTITIES(true),
	WORLD_TRACKED_ENTITIES(true),
	
	MOB_SPAWNING(false, 1.0E-6D),
	BLOCK_UPDATE(false, 1.0E-6D),
	BUILD_ACTIVE_CHUNKSET(false, 1.0E-6D),
	CHECK_PLAYER_LIGHT(false, 1.0E-6D),
	TIME_SYNC(false, 1.0E-6D),
	
	MOOD_LIGHT_AND_WEATHER(false, 1.0E-6D),
	LIGHTNING_AND_RAIN(false, 1.0E-6D),

	ENTITIES_SECTION(false, 1.0E-6D),
		WEATHER_EFFECTS(false, 1.0E-6D),
		ENTITIES_REMOVE(false, 1.0E-6D),
		ENTITIES_REGULAR(false, 1.0E-6D),
			ENTITY_UPDATE(false, 1.0E-6D),
		ENTITIES_TILE(false, 1.0E-6D),
			TILE_ENTITY_UPDATE(false, 1.0E-6D),
		ENTITIES_TILEPENDING(false, 1.0E-6D),
		
		UPDATE_TRACKED_ENTITY_PLAYER_LISTS(false, 1.0E-6D),
		UPDATE_PLAYER_ENTITIES(false, 1.0E-6D),
		UPDATE_TRACKED_ENTITY_PLAYER_LIST(false, 1.0E-6D),
	
	LOAD_CHUNK(false),
	LOAD_CHUNK_TIME(false),
	SPAWN_LIVING(false);
	
	public static AtomicLong bytesSent = new AtomicLong();
	public static AtomicLong bytesReceived = new AtomicLong();
	
	public volatile boolean enabled = true;
	
	public final boolean defaultEnabled;
	public final double scale;
	public final int averageResolution = Average.RESOLUTION;
	
	private Stat(boolean defaultEnabled) {
		this(defaultEnabled, 1.0D);
	}
	
	private Stat(boolean defaultEnabled, double scale) {
		this.defaultEnabled = defaultEnabled;
		this.scale = scale;
	}
	
	public void begin(World world) {
		if (enabled)
			StatsAPI.begin(new StatWorld(this, world));
	}
	
	public void end() {
		if (enabled)
			StatsAPI.end(this);
	}
	
	public static void setEnabled(EnumMap<Stat, Boolean> profile) {
		for (Stat stat : values()) {
			Boolean enabled = profile.get(stat);
			stat.enabled = enabled == null ? stat.defaultEnabled : enabled.booleanValue();
		}
	}
	
	public static void beginBlockUpdate(World world, Block block, int x, int y, int z) {
		if (BLOCK_UPDATE.enabled)
			StatsAPI.begin(new StatPositionedClass(BLOCK_UPDATE, world,
				x, y, z, block.getClass(), block.blockID));
	}

	public static void beginBlockUpdate(World world, NextTickListEntry blockUpdate) {
		if (BLOCK_UPDATE.enabled)
			StatsAPI.begin(new StatPositionedClass(BLOCK_UPDATE, world,
				blockUpdate.xCoord, blockUpdate.yCoord, blockUpdate.zCoord, Block.blocksList[blockUpdate.blockID].getClass(), blockUpdate.blockID));
	}

	public static void beginEntityUpdate(World world, Entity entity) {
		if (ENTITY_UPDATE.enabled)
			StatsAPI.begin(new StatPositionedClass(ENTITY_UPDATE, world,
				entity.posX, entity.posY, entity.posZ, entity.getClass(), entity.entityId));
	}

	public static void beginTileEntityUpdate(World world, TileEntity tileEntity) {
		if (TILE_ENTITY_UPDATE.enabled)
			StatsAPI.begin(new StatPositionedClass(TILE_ENTITY_UPDATE, world,
					tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, tileEntity.getClass()));
	}

	public static void beginUpdateTrackedEntityPlayerList(World world, EntityTrackerEntry trackerEntry) {
		if (UPDATE_TRACKED_ENTITY_PLAYER_LIST.enabled)
			StatsAPI.begin(new StatPositionedClass(UPDATE_TRACKED_ENTITY_PLAYER_LIST, world,
					trackerEntry.trackedEntity.posX, trackerEntry.trackedEntity.posY, trackerEntry.trackedEntity.posZ, trackerEntry.trackedEntity.getClass(), trackerEntry.trackedEntity.entityId));
	}

	public static void beginLoadChunk(World world, int chunkX, int chunkY) {
		if (LOAD_CHUNK.enabled)
			StatsAPI.begin(new StatChunk(LOAD_CHUNK, world, chunkX, chunkY));
	}

	public static void recordSpawning(World world, int spawned) {
		if (SPAWN_LIVING.enabled)
			StatsAPI.record(new StatWorldValue(SPAWN_LIVING, world, spawned));
	}
	
	public static void recordNetworkIO(NetworkType type, int bytes) {
		if (type == NetworkType.RECEIVED)
			bytesReceived.addAndGet(bytes);
		else
			bytesSent.addAndGet(bytes);
	}
	
	public static void recordNetworkIO(NetworkType type, int bytes, String username) {
		recordNetworkIO(type, bytes);
		//StatsAPI.record(new StatNetworkPlayer(type, username, bytes));
	}
	
	public static void recordNetworkIO(NetworkType type, int bytes, EntityPlayer player) {
		recordNetworkIO(type, bytes);
		
		//if (player != null)
		//	StatsAPI.record(new StatNetworkPlayer(type, player.username, bytes));
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
