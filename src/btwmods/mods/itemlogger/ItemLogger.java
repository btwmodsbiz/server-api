package btwmods.mods.itemlogger;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import btwmods.BasicFormatter;

public class ItemLogger {
	private static Logger logger = null;
	
	public static Logger GetLogger() {
		if (logger == null) {
			logger = Logger.getLogger("btwmods.mods.ItemLogger");
			
			try {
				FileHandler handler = new FileHandler(new File(".", "itemlogger.%g.log").getPath(), 10 * 1024, 100);
				handler.setFormatter(new BasicFormatter());
				logger.addHandler(handler);
				
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return logger;
	}
}
