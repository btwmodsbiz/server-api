package btwmod.admincommands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import btwmods.ModLoader;
import btwmods.ReflectionAPI;
import btwmods.io.Settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityList;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.WrongUsageException;

public class CommandDumpEntities extends CommandBase {
	
	private File dumpFile = new File(new File("."), "dumpentities.txt");
	private Map<String,Integer> worldNames = null;
	
	private String[] entityMappings = null;
	private Map<String, Class> entityMapping;
	
	private String[] tileEntityMappings = null;
	private Map<String, Class> tileEntityMapping;
	
	public CommandDumpEntities(Settings settings) {
		// Load settings
		if (settings.hasKey("entitydumpfile")) {
			dumpFile = new File(settings.get("entitydumpfile"));
		}
	}

	@Override
	public String getCommandName() {
		return "dumpentities";
	}

	@Override
	public String getCommandUsage(ICommandSender par1iCommandSender) {
		return "/" + getCommandName() + " <dimension> [\"tile\"] [<class>]";
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			getWorldNames();
			Set keys = worldNames.keySet();
			return getListOfStringsMatchingLastWord(args, (String[])keys.toArray(new String[keys.size()]));
		}
		else if (args.length == 2) {
			getEntityMappings();
			return getListOfStringsMatchingLastWord(args, entityMappings);
		}
		else if (args.length == 3 && args[1].equalsIgnoreCase("tile")) {
			getTileEntityMappings();
			return getListOfStringsMatchingLastWord(args, tileEntityMappings);
		}
		
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length < 1 || args.length > 3 || !worldNames.containsKey(args[0]))
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		
		boolean doTile = args.length > 1 && args[1].equalsIgnoreCase("tile");
		
		JsonArray json = new JsonArray();
		
		Iterator iterator;
		Class classFilter = null;
		World world = MinecraftServer.getServer().worldServers[worldNames.get(args[0]).intValue()];
		
		int count = 0;
		int total = doTile ? world.loadedTileEntityList.size() : world.loadedEntityList.size();
				
		if (doTile) {
			getTileEntityMappings();
			classFilter = args.length < 3 ? null : tileEntityMapping.get(args[2]);
			iterator = world.loadedTileEntityList.iterator();
		}
		else {
			getEntityMappings();
			classFilter = args.length < 2 ? null : entityMapping.get(args[1]);
			iterator = world.loadedEntityList.iterator();
		}
		
		while (iterator.hasNext()) {
			Object next = iterator.next();
			if (next instanceof Entity) {
				Entity entity = (Entity)next;
				if (classFilter == null || classFilter.isAssignableFrom(entity.getClass())) {
					JsonObject entityJson = new JsonObject();
					entityJson.addProperty("type", "Entity");
					entityJson.addProperty("class", entity.getClass().getSimpleName());
					entityJson.addProperty("name", entity.getEntityName());
					entityJson.addProperty("id", entity.entityId);
					entityJson.addProperty("x", entity.posX);
					entityJson.addProperty("y", entity.posY);
					entityJson.addProperty("z", entity.posZ);
					json.add(entityJson);
					count++;
				}
			}
			else if (next instanceof TileEntity) {
				TileEntity tileEntity = (TileEntity)next;
				if (classFilter == null || classFilter.isAssignableFrom(tileEntity.getClass())) {
					JsonObject tileEntityJson = new JsonObject();
					tileEntityJson.addProperty("type", "TileEntity");
					tileEntityJson.addProperty("class", tileEntity.getClass().getSimpleName());
					tileEntityJson.addProperty("x", tileEntity.xCoord);
					tileEntityJson.addProperty("y", tileEntity.yCoord);
					tileEntityJson.addProperty("z", tileEntity.zCoord);
					json.add(tileEntityJson);
					count++;
				}
			}
			else {
				JsonObject unknownJson = new JsonObject();
				unknownJson.addProperty("type", "Unknown");
				unknownJson.addProperty("class", next.getClass().getSimpleName());
				json.add(unknownJson);
			}
		}
			
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(dumpFile));
			writer.write(json.toString());
			writer.close();
			sender.sendChatToPlayer("Dumped " + count + " of " + total + (doTile ? " tile" : "") + " entities.");
			
		} catch (IOException e) {
			sender.sendChatToPlayer("Failed to dump entities: " + e.getMessage());
		}
	}
	
	private void getWorldNames() {
		if (worldNames == null) {
			worldNames = new HashMap<String, Integer>();
			World[] worlds = MinecraftServer.getServer().worldServers;
			for (int i = 0; i < worlds.length; i++) {
				worldNames.put(worlds[i].provider.getDimensionName().replaceAll("[ \\t]+", ""), new Integer(i));
			}
		}
	}
	
	/**
	 * Attempt to get the string to class mappings.
	 */
	private void getEntityMappings() {
		if (entityMappings == null) {
			Map<String, Class> entityMapping = null;
			
			try {
				Field entityMappingField = ReflectionAPI.getPrivateField(EntityList.class, "stringToClassMapping");
				if (entityMappingField != null) {
					entityMapping = (Map<String, Class>)entityMappingField.get(null);
					
					Set keys = entityMapping.keySet();
					entityMappings = (String[])keys.toArray(new String[keys.size()]);
				}
			} catch (IllegalAccessException e) {
				ModLoader.outputError(e, CommandDumpEntities.class.getSimpleName() + " failed to get the entity class mappings: " + e.getMessage());
			}
			
			this.entityMapping = entityMapping;
			
			if (entityMappings == null)
				entityMappings = new String[0];
		}
	}
	
	private void getTileEntityMappings() {
		if (tileEntityMappings == null) {
			Map<String, Class> tileEntityMapping = null;
			
			try {
				Field entityMappingField = ReflectionAPI.getPrivateField(TileEntity.class, "nameToClassMap");
				if (entityMappingField != null) {
					tileEntityMapping = (Map<String, Class>)entityMappingField.get(null);
					
					Set keys = tileEntityMapping.keySet();
					tileEntityMappings = (String[])keys.toArray(new String[keys.size()]);
				}
			} catch (IllegalAccessException e) {
				ModLoader.outputError(e, CommandDumpEntities.class.getSimpleName() + " failed to get the tile entity class mappings: " + e.getMessage());
			}
			
			this.tileEntityMapping = tileEntityMapping;
			
			if (tileEntityMappings == null)
				tileEntityMappings = new String[0];
		}
	}

}
