package btwmods.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SettingsReader {
	public static Map readSettings(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Map settings = new LinkedHashMap<String, String>();

		String section = "";
		String line;
		int equalsIndex;
		while ((line = reader.readLine()) != null) {
			// Trim leading whitespace.
			line = line.replaceAll("^[ \t]+", "");
			
			if (!line.startsWith("#")) {
				if (line.startsWith("[") && line.trim().endsWith("]")) {
					section = line.trim();
				}
				else if ((equalsIndex = line.indexOf('=')) >= 0) {
					settings.put(line.substring(0, equalsIndex), line.substring(equalsIndex + 1));
				}
			}
		}
		reader.close();
		
		return null;
	}
	
	public static boolean isBoolean(String setting) {
		setting = setting.trim().toLowerCase();
		return setting == "yes" || setting == "no" || setting == "true" || setting == "false" || setting == "1" || setting == "0" || setting == "on" || setting == "off";
	}
	
	public static boolean getBooleanValue(String setting) {
		return setting == "yes" || setting == "true" || setting == "1" || setting == "on";
	}
}
