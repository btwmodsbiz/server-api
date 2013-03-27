package btwmods;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.AnvilChunkLoader;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.ChunkProviderServer;
import net.minecraft.src.EntityTracker;
import net.minecraft.src.LongHashMap;
import net.minecraft.src.World;
import btwmods.events.EventDispatcher;
import btwmods.events.EventDispatcherFactory;
import btwmods.events.IAPIListener;
import btwmods.io.Settings;
import btwmods.world.BlockEvent;
import btwmods.world.ChunkEvent;
import btwmods.world.IBlockListener;
import btwmods.world.IChunkListener;
import btwmods.world.IWorldTickListener;
import btwmods.world.WorldTickEvent;

public class WorldAPI {
	private static EventDispatcher listeners = EventDispatcherFactory.create(new Class[] {
		IBlockListener.class, IWorldTickListener.class, IChunkListener.class
	});
	
	private static MinecraftServer server;
	
	private static List[] loadedChunks;
	private static LongHashMap[] id2ChunkMap;
	private static Set[] droppedChunksSet;
	private static Set[] trackedEntitiesSet;

	private static AnvilChunkLoader[] chunkLoaders;
	private static File[] chunkLoaderLocations;
	
	private WorldAPI() { }
	
	public static void init(@SuppressWarnings("unused") Settings settings) throws NoSuchFieldException, IllegalAccessException {
		server = MinecraftServer.getServer();
		
		Field loadedChunksField = ReflectionAPI.getPrivateField(ChunkProviderServer.class, "loadedChunks");
		Field id2ChunkMapField = ReflectionAPI.getPrivateField(ChunkProviderServer.class, "id2ChunkMap");
		Field droppedChunksSetField = ReflectionAPI.getPrivateField(ChunkProviderServer.class, "droppedChunksSet");
		Field trackedEntitiesSetField = ReflectionAPI.getPrivateField(EntityTracker.class, "trackedEntitySet");
		
		if (loadedChunksField == null)
			throw new NoSuchFieldException("loadedChunks");
		if (id2ChunkMapField == null)
			throw new NoSuchFieldException("id2ChunkMap");
		if (droppedChunksSetField == null)
			throw new NoSuchFieldException("droppedChunksSet");
		if (trackedEntitiesSetField == null)
			throw new NoSuchFieldException("trackedEntitySet");
		
		loadedChunks = new List[server.worldServers.length];
		id2ChunkMap = new LongHashMap[server.worldServers.length];
		droppedChunksSet = new Set[server.worldServers.length];
		trackedEntitiesSet = new Set[server.worldServers.length];
		
		for (int i = 0; i < server.worldServers.length; i++) {
			ChunkProviderServer provider = (ChunkProviderServer)server.worldServers[i].getChunkProvider();
			loadedChunks[i] = (List)loadedChunksField.get(provider);
			id2ChunkMap[i] = (LongHashMap)id2ChunkMapField.get(provider);
			droppedChunksSet[i] = (Set)droppedChunksSetField.get(provider);
			trackedEntitiesSet[i] = (Set)trackedEntitiesSetField.get(server.worldServers[i].getEntityTracker());
		}

		try {
			Field chunkLoaderField = ReflectionAPI.getPrivateField(ChunkProviderServer.class, "chunkLoader");
			Field chunkSaveLocationField = ReflectionAPI.getPrivateField(AnvilChunkLoader.class, "chunkSaveLocation");
			
			chunkLoaders = new AnvilChunkLoader[server.worldServers.length];
			chunkLoaderLocations = new File[server.worldServers.length];
			
			for (int i = 0; i < chunkLoaders.length; i++) {
				chunkLoaders[i] = (AnvilChunkLoader)chunkLoaderField.get(MinecraftServer.getServer().worldServers[i].getChunkProvider());
				chunkLoaderLocations[i] = (File)chunkSaveLocationField.get(chunkLoaders[i]);
			}
		}
		catch (Exception e) {
			ModLoader.outputError(WorldAPI.class.getSimpleName() + " failed (" + e.getClass().getSimpleName() + ") to load the chunkLoaders and chunkSaveLocations: " + e.getMessage());
			chunkLoaders = null;
			chunkLoaderLocations = null;
		}
	}
	
	public static List[] getLoadedChunks() {
		return loadedChunks;
	}
	
	public static LongHashMap[] getCachedChunks() {
		return id2ChunkMap;
	}
	
	public static Set[] getDroppedChunks() {
		return droppedChunksSet;
	}
	
	public static Set[] getTrackedEntities() {
		return trackedEntitiesSet;
	}
	
	public static AnvilChunkLoader getAnvilChunkLoader(int worldIndex) {
		if (chunkLoaders != null && worldIndex >= 0 && worldIndex < chunkLoaders.length && chunkLoaders[worldIndex] != null) {
			return chunkLoaders[worldIndex];
		}
		
		return null;
	}
	
	public static File getAnvilSaveLocation(int worldIndex) {
		if (chunkLoaders != null && worldIndex >= 0 && worldIndex < chunkLoaders.length && chunkLoaders[worldIndex] != null) {
			return chunkLoaderLocations[worldIndex];
		}
		
		return null;
	}

	public static void addListener(IAPIListener listener) {
		listeners.addListener(listener);
	}

	public static void removeListener(IAPIListener listener) {
		listeners.removeListener(listener);
	}

	public static void onStartTick(int worldIndex) {
		WorldTickEvent event = WorldTickEvent.StartTick(worldIndex);
		((IWorldTickListener)listeners).onWorldTick(event);
	}

	public static void onEndTick(int worldIndex) {
		WorldTickEvent event = WorldTickEvent.EndTick(worldIndex);
		((IWorldTickListener)listeners).onWorldTick(event);
	}
	
	public static void onBlockBroken(World world, Chunk chunk, Block block, int x, int y, int z, @SuppressWarnings("unused") int blockID, int blockMetadata) {
		BlockEvent event = BlockEvent.Broken(world, chunk, block, blockMetadata, x, y, z);
		((IBlockListener)listeners).onBlockAction(event);
	}

	public static boolean onBlockExplodeAttempt(World world, int blockId, int x, int y, int z) {
		if (!listeners.isEmpty(IBlockListener.class)) {
			BlockEvent event = BlockEvent.ExplodeAttempt(world, blockId, x, y, z);
			((IBlockListener)listeners).onBlockAction(event);
			
			if (!event.isAllowed())
				return false;
		}
		
		return true;
	}

	public static boolean onDestroyBlockWithFireAttempt(World world, int blockId, int x, int y, int z) {
		if (!listeners.isEmpty(IBlockListener.class)) {
			BlockEvent event = BlockEvent.BurnAttempt(world, blockId, x, y, z);
			((IBlockListener)listeners).onBlockAction(event);
			
			if (!event.isAllowed())
				return false;
		}
		
		return true;
	}

	public static boolean onGetChanceOfNeighborsEncouragingFireAllowed(World world, int x, int y, int z) {
		if (!listeners.isEmpty(IBlockListener.class)) {
			BlockEvent event = BlockEvent.FireSpreadAttempt(world, x, y, z);
			((IBlockListener)listeners).onBlockAction(event);
			
			if (!event.isAllowed())
				return false;
		}
		
		return true;
	}

	public static boolean onIsFlammableBlock(World world, int x, int y, int z) {
		if (!listeners.isEmpty(IBlockListener.class)) {
			BlockEvent event = BlockEvent.IsFlammableBlock(world, x, y, z);
			((IBlockListener)listeners).onBlockAction(event);
			
			if (!event.isFlammable())
				return false;
		}
		
		return true;
	}

	public static boolean onCanPushBlock(World world, int orientation, int pistonX, int pistonY, int pistonZ, int blockX, int blockY, int blockZ) {
		if (!listeners.isEmpty(IBlockListener.class)) {
			BlockEvent event = BlockEvent.CanPushBlock(world, orientation, pistonX, pistonY, pistonZ, blockX, blockY, blockZ);
			((IBlockListener)listeners).onBlockAction(event);
			
			if (!event.isAllowed())
				return false;
		}
		
		return true;
	}

	public static void onUnloadedChunk(World world, Chunk chunk) {
		ChunkEvent event = ChunkEvent.Unloaded(world, chunk);
		((IChunkListener)listeners).onChunkAction(event);
	}

	public static void onPreUnloadChunk(World world, Chunk chunk) {
		ChunkEvent event = ChunkEvent.PreUnload(world, chunk);
		((IChunkListener)listeners).onChunkAction(event);
	}

	public static int onSpawnCustom(HashMap<ChunkCoordIntPair, Boolean> eligibleChunksForSpawning) {
		return 0;
	}
}
