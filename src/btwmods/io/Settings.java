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
	
	private final Map<String, String> settings = new LinkedHashMap<String, String>();
	private final Map<String, String> lowercaseLookup = new LinkedHashMap<String, String>();
	
	private final Map<String, Set<String>> sectionKeys = new LinkedHashMap<String, Set<String>>();
	private final Map<String, String> sectionCaseLookup = new LinkedHashMap<String, String>();
	
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
		if (section != null)
			key = "[" + section + "]" + key;
		
		if (hasKey(key)) {
			String setting = get(key).trim();
			return setting.equalsIgnoreCase("yes") || setting.equalsIgnoreCase("true") || setting.equalsIgnoreCase("1") || setting.equalsIgnoreCase("on")
					 || setting.equalsIgnoreCase("no") || setting.equalsIgnoreCase("false") || setting.equalsIgnoreCase("0") || setting.equalsIgnoreCase("off");
		}
		return false;
	}
	
	public boolean getBoolean(String key) {
		return getBoolean(null, key);
	}
	
	public boolean getBoolean(String section, String key) {
		if (section != null)
			key = "[" + section + "]" + key;
		
		if (!isBoolean(key)) throw new IllegalArgumentException("setting is not a valid boolean. check with isBoolean() first");
		String setting = get(key).trim();
		return setting.equalsIgnoreCase("yes") || setting.equalsIgnoreCase("true") || setting.equalsIgnoreCase("1") || setting.equalsIgnoreCase("on");
	}
	
	public boolean isInt(String key) {
		return isInt(null, key);
	}
	
	public boolean isInt(String section, String key) {
		if (section != null)
			key = "[" + section + "]" + key;
		
		try { return hasKey(key) && Integer.valueOf(get(key)) != null; }
		catch (NumberFormatException e) { return false; }
	}
	
	public int getInt(String key) {
		return getInt(null, key);
	}
	
	public int getInt(String section, String key) {
		if (section != null)
			key = "[" + section + "]" + key;
		
		if (!isInt(key)) throw new IllegalArgumentException("setting is not a valid Integer. check with isInt() first");
		return Integer.parseInt(get(key));
	}
	
	public boolean isLong(String key) {
		return isLong(null, key);
	}
	
	public boolean isLong(String section, String key) {
		if (section != null)
			key = "[" + section + "]" + key;
		
		try { return hasKey(key) && Long.valueOf(get(key)) != null; }
		catch (NumberFormatException e) { return false; }
	}
	
	public long getLong(String key) {
		return getLong(null, key);
	}
	
	public long getLong(String section, String key) {
		if (section != null)
			key = "[" + section + "]" + key;
		
		if (!isLong(key)) throw new IllegalArgumentException("setting is not a valid Long. check with isLong() first");
		return Long.parseLong(get(key));
	}
	
	public boolean hasKey(String key) {
		return hasKey(null, key);
	}
	
	public boolean hasKey(String section, String key) {
		if (section != null)
			key = "[" + section + "]" + key;
		
		return lowercaseLookup.containsKey(key.toLowerCase());
	}
	
	public boolean hasSection(String section) {
		return sectionCaseLookup.containsKey(section == null ? null : section.toLowerCase());
	}
	
	public String get(String key) {
		return get(null, key);
	}
	
	public String get(String section, String key) {
		if (section != null)
			key = "[" + section + "]" + key;
		
		String lookupKey = lowercaseLookup.get(key.toLowerCase());
		return lookupKey == null ? null : settings.get(lookupKey);
	}
	
	public Set<String> getSections() {
		return sectionKeys.keySet();
	}
	
	public Set<String> getSectionKeys(String section) {
		section = section == null ? null : section.toLowerCase();
		return sectionCaseLookup.containsKey(section) ? sectionKeys.get(sectionCaseLookup.get(section)) : null;
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
	
	public void set(String key, String value) {
		set(null, key, value);
	}
	
	public void set(String section, String key, String value) {
		if (key == null)
			throw new NullPointerException();
		
		if (value == null)
			throw new NullPointerException();
		
		String fullKey = section == null ? key : "[" + section + "]" + key;
		
		// Make sure the full key is in the case-insensitive lookup.
		if (!lowercaseLookup.containsKey(fullKey.toLowerCase()))
			lowercaseLookup.put(fullKey.toLowerCase(), fullKey);
		
		// Make sure the section name is in the case-insensitive lookup.
		if (section != null && !sectionCaseLookup.containsKey(section.toLowerCase()))
			sectionCaseLookup.put(section.toLowerCase(), section);
		
		// Make sure we are maintaining a set of keys for the section.
		if (!sectionKeys.containsKey(section))
			sectionKeys.put(section, new LinkedHashSet<String>());
		
		// Add the key to the section key set.
		sectionKeys.get(section).add(key);
		
		// Add the full key and value.
		settings.put(fullKey, value);
	}

	public boolean removeSection(String section) {
		section = section == null ? null : section.toLowerCase();
		if (sectionCaseLookup.containsKey(section)) {
			Set<String> keys = sectionKeys.get(sectionCaseLookup.get(section));
			if (keys != null)
				for (String key : keys)
					removeKey(section, key);
			
			sectionCaseLookup.remove(section);
			sectionKeys.remove(section);
			return true;
		}
		
		return false;
	}

	public void removeKey(String key) {
		removeKey(null, key);
	}

	public void removeKey(String section, String key) {
		if (section != null)
			key = "[" + section + "]" + key;
		
		lowercaseLookup.remove(key.toLowerCase());
		settings.remove(key);
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
		
		for (Map.Entry<String, String> entry : settings.entrySet()) {
			String section = null;
			String key = entry.getKey();
			
			if (key.startsWith("[")) {
				int endSectionIndex = key.indexOf(']', 1);
				section = key.substring(1, endSectionIndex);
				key = key.substring(endSectionIndex + 1);
			}
			
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
			writer.write("[" + sectionEntry.getKey() + "]");
			writer.newLine();
			
			for (Map.Entry<String, String> pairEntry : sectionEntry.getValue().entrySet()) {
				writer.write("\t" + pairEntry.getKey() + "=" + pairEntry.getValue());
				writer.newLine();
			}
			
			writer.newLine();
		}
		
		writer.close();
	}
}
