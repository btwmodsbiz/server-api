package btwmods;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import net.minecraft.server.MinecraftServer;

public class ModProperties {
	private static Properties properties = null;
	private static File propertiesFile = null;
	
	public static final boolean Default_EnableHardcoreBeds = true;
	
	private static void Load() {
		if (properties == null) {
			properties = new Properties();
			
			// Some basic defaults.
			//properties.setProperty(PlayerAPI.MOD_LIST_KEY, "");
			//properties.setProperty(WorldAPI.MOD_LIST_KEY, "");
			
			propertiesFile = new File(".", "btwmod.properties");
			
			// Save the default properties, if the file does not exist.
			if (!propertiesFile.isFile()) {
				try {
					Save();
					MinecraftServer.logger.info("Saved empty BTWMod config to btwmod.properties");
				} catch (IOException e) {
					MinecraftServer.logger.warning("Failed to save empty BTWMod config to btwmod.properties: " + e.getMessage());
				}
			}
			else {
				try {
					properties.load(new FileReader(propertiesFile));
					MinecraftServer.logger.info("Loaded BTWMod config.");
				}
				catch (IOException e) {
					MinecraftServer.logger.warning("Failed to load BTWMod config: " + e.getMessage());
				}
			}
		}
	}
	
	private static void Save() throws IOException {
		properties.store(new FileWriter(propertiesFile), "Do not edit this file while the Minecraft server is running!");
	}
	
	public static String Get(String key, String defaultValue) {
		Load();
		String value = properties.getProperty(key);
		if (value == null && defaultValue != null) {
			properties.setProperty(key, defaultValue);
			try {
				Save();
			}
			catch (IOException e) {
				MinecraftServer.logger.warning("Failed to save a default value for a BTWMod config (" + key + "=" + defaultValue + ") to btwmod.properties: " + e.getMessage());
			}
		}
		return value;
	}
	
	public static boolean GetBoolean(String key, boolean defaultValue) {
		String value = Get(key, "1");
		return value == "1";
	}
}
