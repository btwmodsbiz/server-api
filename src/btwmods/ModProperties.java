package btwmods;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Properties;

import btwmods.api.player.PlayerAPI;

import net.minecraft.server.MinecraftServer;

public class ModProperties {
	private static Properties properties = null;
	private static File propertiesFile = null;
	
	private static void Load() {
		if (properties == null) {
			properties = new Properties();
			
			// Defaults
			properties.setProperty(PlayerAPI.MOD_LIST_KEY, "");
			PlayerAPI.SetDefaultProperties(properties);
			
			propertiesFile = new File(".", "btwmod.properties");
			
			// Save the default properties, if the file does not exist.
			if (!propertiesFile.isFile()) {
				try {
					properties.store(new FileWriter(propertiesFile), null);
					MinecraftServer.logger.info("Saved default BTWMod config to btwmod.properties.");
				} catch (IOException e) {
					MinecraftServer.logger.warning("Failed to save default BTWMod config to btwmod.properties: " + e.getMessage());
				}
			}
			
			try {
				properties.load(new FileReader(propertiesFile));
				MinecraftServer.logger.info("Loaded BTWMod config.");
			}
			catch (IOException e) {
				MinecraftServer.logger.warning("Failed to load BTWMod config: " + e.getMessage());
			}
		}
	}
	
	public static String Get(String key) {
		Load();
		return properties.getProperty(key);
	}
}
