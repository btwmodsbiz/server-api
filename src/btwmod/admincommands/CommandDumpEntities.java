package btwmod.admincommands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityList;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.WrongUsageException;

public class CommandDumpEntities extends CommandBase {
	
	private File dumpFile = new File(new File("."), "dumpentities.txt");
	private Map<String,Integer> worldNames = null;
	
	private List<String> entityNames = null;
	private Map<String, Class> entityNameMap = null;
	
	private List<String> tileEntityNames = null;
	private Map<String, Class> tileEntityMap = null;
	
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
		return "/" + getCommandName() + " <dimension> [\"tile\"] [<class> ...]";
	}

	@Override
	public List getCommandAliases() {
        return Arrays.asList(new String[] { "entities" });
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			getWorldNames();
			Set keys = worldNames.keySet();
			return getListOfStringsMatchingLastWord(args, (String[])keys.toArray(new String[keys.size()]));
		}
		else if (args.length >= 2 && !args[1].equalsIgnoreCase("tile")) {
			getEntityMappings();
			return getListOfStringsMatchingLastWord(args, entityNames.toArray(new String[entityNames.size()]));
		}
		else if (args.length >= 3 && args[1].equalsIgnoreCase("tile")) {
			getTileEntityMappings();
			return getListOfStringsMatchingLastWord(args, tileEntityNames.toArray(new String[tileEntityNames.size()]));
		}
		
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length < 1 || !worldNames.containsKey(args[0]))
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		
		boolean doTile = args.length > 1 && args[1].equalsIgnoreCase("tile");
		
		JsonArray json = new JsonArray();
		World world = MinecraftServer.getServer().worldServers[worldNames.get(args[0]).intValue()];
		Iterator iterator;
		
		int count = 0;
		int total = doTile ? world.loadedTileEntityList.size() : world.loadedEntityList.size();
				
		if (doTile) {
			getTileEntityMappings();
			iterator = world.loadedTileEntityList.iterator();
		}
		else {
			getEntityMappings();
			iterator = world.loadedEntityList.iterator();
		}
		
		List<Class> classFilters = new ArrayList<Class>();
		for (int i = doTile ? 2 : 1; i < args.length; i++) {
			Class filterClass = doTile ? tileEntityMap.get(args[i]) : entityNameMap.get(args[i]);
			
			if (filterClass == null)
				throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
			
			classFilters.add(filterClass);
		}
		
		while (iterator.hasNext()) {
			Object next = iterator.next();
			if (next instanceof Entity) {
				Entity entity = (Entity)next;
				if (classFilters.size() == 0 || isAssignableFrom(entity.getClass(), classFilters)) {
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
				if (classFilters.size() == 0 || isAssignableFrom(tileEntity.getClass(), classFilters)) {
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
	
	@SuppressWarnings("static-method")
	private boolean isAssignableFrom(Class cls, List<Class> classes) {
		for (Class filter : classes) {
			if (filter.isAssignableFrom(cls))
				return true;
		}
		return false;
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
		if (entityNames == null) {
			entityNameMap = new HashMap<String, Class>();
			
			try {
				Field field = ReflectionAPI.getPrivateField(EntityList.class, "stringToClassMapping");
				if (field != null) {
					entityNameMap.putAll((Map<String, Class>)field.get(null));
				}
				else {
					ModLoader.outputError(CommandDumpEntities.class.getSimpleName() + " failed to get the entity class mapping field.");
				}
			} catch (IllegalAccessException e) {
				ModLoader.outputError(e, CommandDumpEntities.class.getSimpleName() + " failed to get the entity class mapping instance: " + e.getMessage());
			}

			entityNameMap.put("Player", EntityPlayer.class);
			entityNameMap.put("Animal", EntityAnimal.class);
			entityNameMap.put("Living", EntityLiving.class);
			
			entityNames = new ArrayList<String>(entityNameMap.keySet());
			Collections.sort(entityNames);
		}
	}
	
	private void getTileEntityMappings() {
		if (tileEntityNames == null) {
			tileEntityMap = new HashMap<String, Class>();
			tileEntityNames = new ArrayList<String>();
			
			try {
				Field field = ReflectionAPI.getPrivateField(TileEntity.class, "nameToClassMap");
				if (field != null) {
					tileEntityMap.putAll((Map<String, Class>)field.get(null));
				}
				else {
					ModLoader.outputError(CommandDumpEntities.class.getSimpleName() + " failed to get the tile entity class mapping field.");
				}
			} catch (IllegalAccessException e) {
				ModLoader.outputError(e, CommandDumpEntities.class.getSimpleName() + " failed to get the tile entity class mapping instance: " + e.getMessage());
			}
			
			tileEntityNames = new ArrayList<String>(tileEntityMap.keySet());
			Collections.sort(tileEntityNames);
		}
	}

}