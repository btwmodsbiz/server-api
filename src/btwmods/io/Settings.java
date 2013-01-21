package btwmods.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import btwmods.util.CaselessKey;

public class Settings {
	
	public static Settings readSavableSettings(File file) throws IOException {
		Settings settings = readSettings(file);
		settings.setSaveTarget(file);
		return settings;
	}
	
	public static Settings readSettings(File file) throws IOException {
		FileReader reader = null;
		try {
			reader = new FileReader(file);
			return readSettings(new BufferedReader(reader));
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (IOException e) { }
		}
	}
	
	public static Settings readSettings(String settings) {
		try {
			return readSettings(new BufferedReader(new StringReader(settings)));
		}
		catch (IOException e) {
			return new Settings();
		}
	}
	
	public static Settings readSettings(BufferedReader reader) throws IOException {
		Settings settings = new Settings();

		String section = null;
		String line;
		int equalsIndex;
		
		while ((line = reader.readLine()) != null) {
			// Trim leading whitespace.
			line = line.replaceAll("^[ \t]+", "");
			
			if (!line.startsWith("#")) {
				if (line.trim().startsWith("[") && line.trim().endsWith("]")) {
					section = line.trim().substring(1, line.trim().length() - 1);
				}
				else if ((equalsIndex = line.indexOf('=')) >= 0) {
					settings.set(section, line.substring(0, equalsIndex), line.substring(equalsIndex + 1));
				}
			}
		}
		
		return settings;
	}
	
	private final Map<SectionKeyPair, String> settings = new LinkedHashMap<SectionKeyPair, String>();
	private final Map<CaselessKey, Set<CaselessKey>> sectionKeys = new LinkedHashMap<CaselessKey, Set<CaselessKey>>();
	
	private File saveTarget = null;
	
	public Settings() { }
	
	public Settings(File saveTarget) {
		this.saveTarget = saveTarget;
	}
	
	public boolean isSavable() {
		return saveTarget != null;
	}
	
	public void setSaveTarget(File file) {
		saveTarget = file;
	}
	
	public boolean isBoolean(String key) {
		return isBoolean(null, key);
	}
	
	public boolean isBoolean(String section, String key) {
		return isBooleanValue(get(section, key));
	}
	
	public static boolean isBooleanValue(String value) {
		if (value != null) {
			value = value.trim();
			return value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1") || value.equalsIgnoreCase("on")
					 || value.equalsIgnoreCase("no") || value.equalsIgnoreCase("false") || value.equalsIgnoreCase("0") || value.equalsIgnoreCase("off");
		}
		
		return false;
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
		return getBoolean(null, key, defaultValue);
	}
	
	public boolean getBoolean(String section, String key, boolean defaultValue) {
		return getBooleanValue(get(section, key), defaultValue);
	}
	
	public static boolean getBooleanValue(String value, boolean defaultValue) {
		if (value != null) {
			value = value.trim();
			return value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1") || value.equalsIgnoreCase("on");
		}
		
		return defaultValue;
	}
	
	public boolean isInt(String key) {
		return isInt(null, key);
	}
	
	public boolean isInt(String section, String key) {
		return isIntValue(get(section, key));
	}
	
	public static boolean isIntValue(String value) {
		try {
			return value != null && Integer.valueOf(value) != null;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public int getInt(String key, int defaultValue) {
		return getInt(null, key, defaultValue);
	}
	
	public int getInt(String section, String key, int defaultValue) {
		try {
			String setting = get(section, key);
			return setting == null ? defaultValue : Integer.parseInt(setting);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public boolean isLong(String key) {
		return isLong(null, key);
	}
	
	public boolean isLong(String section, String key) {
		return isLongValue(get(section, key));
	}
	
	public static boolean isLongValue(String value) {
		try {
			return value != null && Long.valueOf(value) != null;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public long getLong(String key, long defaultValue) {
		return getLong(null, key, defaultValue);
	}
	
	public long getLong(String section, String key, long defaultValue) {
		try {
			String setting = get(section, key);
			return setting == null ? defaultValue : Long.parseLong(setting);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public boolean isFloat(String key) {
		return isFloat(null, key);
	}
	
	public boolean isFloat(String section, String key) {
		return isFloatValue(get(section, key));
	}
	
	public static boolean isFloatValue(String value) {
		try {
			return value != null && Float.valueOf(value) != null;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public float getFloat(String key, float defaultValue) {
		return getFloat(null, key, defaultValue);
	}
	
	public float getFloat(String section, String key, float defaultValue) {
		try {
			String setting = get(section, key);
			return setting == null ? defaultValue : Float.parseFloat(setting);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public boolean isDouble(String key) {
		return isDouble(null, key);
	}
	
	public boolean isDouble(String section, String key) {
		return isDoubleValue(get(section, key));
	}
	
	public static boolean isDoubleValue(String value) {
		try {
			return value != null && Double.valueOf(value) != null;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public double getDouble(String key, double defaultValue) {
		return getDouble(null, key, defaultValue);
	}
	
	public double getDouble(String section, String key, double defaultValue) {
		try {
			String setting = get(section, key);
			return setting == null ? defaultValue : Double.parseDouble(setting);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public <T extends Enum<T>> boolean isEnum(Class<T> cls, String key) {
		return isEnum(cls, null, key);
	}
	
	public <T extends Enum<T>> boolean isEnum(Class<T> cls, String section, String key) {
		return isEnumValue(cls, get(section, key));
	}
	
	public static <T extends Enum<T>> boolean isEnumValue(Class<T> cls, String value) {
		try {
			Enum.valueOf(cls, value);
			return true;
		}
		catch (IllegalArgumentException x) {
			return false;
		}
	}
	
	public <T extends Enum<T>> T getEnum(Class<T> cls, String key) {
		return getEnum(cls, null, key, null);
	}
	
	public <T extends Enum<T>> T getEnum(Class<T> cls, String section, String key) {
		return getEnum(cls, section, key, null);
	}
	
	public <T extends Enum<T>> T getEnum(Class<T> cls, String key, T defaultValue) {
		return getEnum(cls, null, key, defaultValue);
	}
	
	public <T extends Enum<T>> T getEnum(Class<T> cls, String section, String key, T defaultValue) {
		return getEnumValue(cls, get(section, key), defaultValue);
	}
	
	public static <T extends Enum<T>> T getEnumValue(Class<T> cls, String value) {
		return getEnumValue(cls, value, null);
	}
	
	public static <T extends Enum<T>> T getEnumValue(Class<T> cls, String value, T defaultValue) {
		try {
			return Enum.valueOf(cls, value);
		}
		catch (NullPointerException x) {
			return defaultValue;
		}
		catch (IllegalArgumentException x) {
			return defaultValue;
		}
	}
	
	public boolean hasKey(String key) {
		return hasKey(null, key);
	}
	
	public boolean hasKey(String section, String key) {
		return settings.containsKey(new SectionKeyPair(section, key));
	}
	
	public boolean hasSection(String section) {
		return sectionKeys.containsKey(section == null ? null : new CaselessKey(section));
	}
	
	public String get(String key) {
		return get(null, key);
	}
	
	public String get(String section, String key) {
		return settings.get(new SectionKeyPair(section, key));
	}
	
	public Set<CaselessKey> getSections() {
		return sectionKeys.keySet();
	}
	
	public Set<CaselessKey> getSectionKeys(String section) {
		return sectionKeys.get(section == null ? null : new CaselessKey(section));
	}
	
	public Settings getSectionAsSettings(String section) {
		Set<CaselessKey> keys = sectionKeys.get(section == null ? null : new CaselessKey(section));
		if (keys != null) {
			Settings settings = new Settings();
			
			for (CaselessKey key : keys)
				settings.set(key.key, get(section, key.key));
			
			return settings;
		}
		
		return null;
	}
	
	public void setBoolean(String key, boolean value) {
		set(key, value ? "yes" : "no");
	}
	
	public void setInt(String key, int value) {
		set(key, Integer.toString(value));
	}
	
	public void setLong(String key, long value) {
		set(key, Long.toString(value));
	}
	
	public void setBoolean(String section, String key, boolean value) {
		set(section, key, value ? "yes" : "no");
	}
	
	public void setInt(String section, String key, int value) {
		set(section, key, Integer.toString(value));
	}
	
	public void setLong(String section, String key, long value) {
		set(section, key, Long.toString(value));
	}
	
	public void setFloat(String section, String key, float value) {
		set(section, key, Float.toString(value));
	}
	
	public void set(String key, String value) {
		set(null, key, value);
	}
	
	public void set(String section, String key, String value) {
		if (key == null)
			throw new NullPointerException();
		
		if (value == null)
			throw new NullPointerException();
		
		// Make sure we are maintaining a set of keys for the section.
		Set<CaselessKey> keys = sectionKeys.get(section == null ? null : new CaselessKey(section));
		if (keys == null)
			sectionKeys.put(section == null ? null : new CaselessKey(section), keys = new LinkedHashSet<CaselessKey>());
		
		// Add the key to the section key set.
		keys.add(new CaselessKey(key));
		
		// Add the section, key and value.
		settings.put(new SectionKeyPair(section, key), value);
	}

	public boolean removeSection(String section) {
		Set<CaselessKey> keys = sectionKeys.get(section == null ? null : new CaselessKey(section));
		if (keys != null) {
			for (CaselessKey key : keys)
				removeKey(section, key.key);
			
			sectionKeys.remove(section == null ? null : new CaselessKey(section));
			return true;
		}
		
		return false;
	}

	public void removeKey(String key) {
		removeKey(null, key);
	}

	public void removeKey(String section, String key) {
		settings.remove(new SectionKeyPair(section, key));
		
		Set<CaselessKey> keys = sectionKeys.get(section == null ? null : new CaselessKey(section));
		if (keys != null) keys.remove(key);
	}
	
	public void saveSettings() throws IOException {
		if (saveTarget == null)
			throw new IOException("Save target has not been set");
		
		writeSettings(saveTarget);
	}
	
	public void writeSettings(File file) throws IOException {
		writeSettings(new BufferedWriter(new FileWriter(file)));
	}
	
	public void writeSettings(BufferedWriter writer) throws IOException {
		Map<String, Map<String, String>> sections = new LinkedHashMap<String, Map<String, String>>();
		
		for (Map.Entry<SectionKeyPair, String> entry : settings.entrySet()) {
			String section = entry.getKey().section;
			String key = entry.getKey().key;
			
			if (!sections.containsKey(section)) {
				sections.put(section, new LinkedHashMap<String, String>());
			}
			
			Map<String, String> sectionMap = sections.get(section);
			sectionMap.put(key, entry.getValue());
		}
		
		if (sections.containsKey(null)) {
			for (Map.Entry<String, String> entry : sections.get(null).entrySet()) {
				writer.write(entry.getKey() + "=" + entry.getValue());
				writer.newLine();
			}
			
			writer.newLine();
		}
		
		for (Map.Entry<String, Map<String, String>> sectionEntry : sections.entrySet()) {
			if (sectionEntry.getKey() != null) {
				writer.write("[" + sectionEntry.getKey() + "]");
				writer.newLine();
				
				for (Map.Entry<String, String> pairEntry : sectionEntry.getValue().entrySet()) {
					writer.write("\t" + pairEntry.getKey() + "=" + pairEntry.getValue());
					writer.newLine();
				}
				
				writer.newLine();
			}
		}
		
		writer.close();
	}
	
	private static class SectionKeyPair {
		public final String section;
		public final String key;
		
		public SectionKeyPair(String section, String key) {
			this.section = section;
			this.key = key;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + key.toLowerCase().hashCode();
			result = prime * result + ((section == null) ? 0 : section.toLowerCase().hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			
			if (obj == null)
				return false;
			
			if (getClass() != obj.getClass())
				return false;
			
			SectionKeyPair other = (SectionKeyPair)obj;
			
			if (key == null) {
				if (other.key != null)
					return false;
			}
			else if (!key.equalsIgnoreCase(other.key)) {
				return false;
			}
			
			if (section == null) {
				if (other.section != null)
					return false;
			}
			else if (!section.equalsIgnoreCase(other.section)) {
				return false;
			}
			
			return true;
		}

		@Override
		public String toString() {
			return (section == null ? "" : "[" + section + "]") + key;
		}
	}

	public void clear() {
		settings.clear();
		sectionKeys.clear();
	}
}
