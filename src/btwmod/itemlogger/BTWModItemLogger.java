package btwmod.itemlogger;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import btwmods.BasicFormatter;
import btwmods.IMod;
import btwmods.PlayerAPI;
import btwmods.WorldAPI;

public class BTWModItemLogger implements IMod {
	
	public static Logger logger = null;
	
	private PlayerListener playerListener;
	private WorldListener worldListener;
	
	@Override
	public String getName() {
		return "Item Logger";
	}

	@Override
	public void init() {
		if (logger == null) {
			logger = Logger.getLogger("btwmod.itemlogger");
			
			try {
				FileHandler handler = new FileHandler(new File(".", "itemlogger.%g.log").getPath(), 10 * 1024, 100);
				handler.setFormatter(new BasicFormatter());
				logger.addHandler(handler);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		PlayerAPI.addListener(playerListener = new PlayerListener(this, logger));
		WorldAPI.addListener(worldListener = new WorldListener(this, logger));
	}

	@Override
	public void unload() {
		PlayerAPI.removeListener(playerListener);
		WorldAPI.removeListener(worldListener);
	}

}
