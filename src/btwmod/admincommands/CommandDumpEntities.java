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
import net.minecraft.src.NumberInvalidException;
import net.minecraft.src.World;
import net.minecraft.src.WrongUsageException;

public class CommandDumpEntities extends CommandBase {
	
	private File dumpFile = new File(new File("."), "dumpentities.txt");
	private Map<String,Integer> worldNames = null;
	private String[] entityMappings = null;
	private Map<String, Class> entityMapping;
	
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
		return "/" + getCommandName() + " <dimension> [<class>]";
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
		
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length < 1 || args.length > 2 || !worldNames.containsKey(args[0]))
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		
		JsonArray json = new JsonArray();
		Class classFilter = args.length < 2 ? null : entityMapping.get(args[1]);
		int count = 0;
		
		try {
			World world = MinecraftServer.getServer().worldServers[worldNames.get(args[0]).intValue()];
			Iterator entities = world.loadedEntityList.iterator();
			while (entities.hasNext()) {
				Object next = entities.next();
				if (next instanceof Entity) {
					Entity entity = (Entity)next;
					if (classFilter == null || classFilter.isAssignableFrom(entity.getClass())) {
						JsonObject entityJson = new JsonObject();
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
				else {
					JsonObject unknownJson = new JsonObject();
					unknownJson.addProperty("unknown", next.getClass().getSimpleName());
					json.add(unknownJson);
				}
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(dumpFile));
			writer.write(json.toString());
			writer.close();
			
			sender.sendChatToPlayer("Dumped " + count + " of " + world.loadedEntityList.size() + " entities.");
		}
		catch (NumberFormatException e) {
			throw new NumberInvalidException("commands.generic.num.invalid", new Object[] { args[0] });
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

}
