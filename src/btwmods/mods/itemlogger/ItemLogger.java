package btwmods.mods.itemlogger;

import java.util.logging.Logger;

public class ItemLogger {
	private static Logger logger = null;
	
	public static Logger GetLogger() {
		if (logger == null) {
			logger = Logger.getLogger("btwmods.mods.ItemLogger");
		}
		
		return logger;
	}
}
