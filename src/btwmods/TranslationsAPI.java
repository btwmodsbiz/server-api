package btwmods;

import java.lang.reflect.Field;
import java.util.Properties;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.StringTranslate;

public class TranslationsAPI {
	
	private static Field playerTranslatorField = null;
	private static Field translateTableField = null;
	private static Properties serverTranslationTable = null;
	
	/**
	 * Note: This should only be called by {@link ModLoader#init()}.
	 * @throws Exception If a reflection exception was thrown.
	 */
	public static void init() throws Exception {
		translateTableField = StringTranslate.class.getField("translateTable");
		translateTableField.setAccessible(true);
		
		playerTranslatorField = EntityPlayerMP.class.getField("translator");
		playerTranslatorField.setAccessible(true);
		
		serverTranslationTable = getTranslationTable(StringTranslate.getInstance());
	}
	
	/**
	 * Get a translation for the specified sender (e.g. {@link EntityPlayer} or {@link MinecraftServer}).
	 * 
	 * @param key The key to lookup.
	 * @param sender
	 * @return The 
	 */
	public static String getTranslation(String key, ICommandSender sender) {
		try {
			return getTranslationTable(sender).getProperty(key);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Get a key that is prefixed with the mod's package name to avoid conflicts.
	 * Example: btwmod.mymod.mykey
	 * 
	 * @param mod The mod that the key is being created for.
	 * @param key The key string that will be appended to the end of the mod's package name.
	 * @return The combined key.
	 */
	public static String getTranslationKey(IMod mod, String key) {
		return ModLoader.getModPackageName(mod) + "." + key;
	}
	
	/**
	 * Get a command key that combines the mod's package name with the command's name to avoid conflicts.
	 * Example: btwmod.mymod.command.mycommand
	 * 
	 * @param mod The mod that the key is being created for.
	 * @param command The command to create the key for.
	 * @return The key for the command.
	 */
	public static String getTranslationKey(IMod mod, CommandBase command) {
		return TranslationsAPI.getTranslationKey(mod, "command." + command.getCommandName());
	}
	
	/**
	 * Set a translation in the specified ICommandSender's translation table.
	 * 
	 * @param sender The ICommandSender to set the translation for.
	 * @param key The key for the translation.
	 * @param value The value for the translation.
	 * @return <code>true</code> if the key was successfully set; <code>false</code> otherwise.
	 */
	public static boolean setTranslation(ICommandSender sender, String key, String value) {
		try {
			Properties translationTable = getTranslationTable(sender);
			translationTable.setProperty(key, value);
			return true;
		}
		catch (IllegalAccessException e) {
		}
		
		return false;
	}
	
	private static Properties getTranslationTable(ICommandSender sender) throws IllegalAccessException {
		if (sender instanceof EntityPlayerMP) {
			
			return getTranslationTable(((EntityPlayerMP)sender).getTranslator());
		}
		else {
			return serverTranslationTable;
		}
	}
	
	private static Properties getTranslationTable(StringTranslate translator) throws IllegalAccessException {
		return (Properties)translateTableField.get(translator);
	}
}
