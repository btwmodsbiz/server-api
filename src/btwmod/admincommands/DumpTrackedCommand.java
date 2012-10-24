package btwmod.admincommands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

import btwmods.ModLoader;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityTracker;
import net.minecraft.src.EntityTrackerEntry;
import net.minecraft.src.ICommandSender;

public class DumpTrackedCommand extends CommandBase {
	
	private static Set[] trackedEntitiesSet;
	
	public DumpTrackedCommand() {
		Field trackedEntitiesSetField;
		MinecraftServer server = MinecraftServer.getServer();
		
		try {
			trackedEntitiesSetField = EntityTracker.class.getDeclaredField("trackedEntitySet");
			trackedEntitiesSetField.setAccessible(true);
			
			trackedEntitiesSet = new Set[server.worldServers.length];
			for (int i = 0; i < server.worldServers.length; i++) {
				trackedEntitiesSet[i] = (Set)trackedEntitiesSetField.get(server.worldServers[i].getEntityTracker());
			}
		} catch (NoSuchFieldException e) {
			ModLoader.outputError(e, "Failed to get trackedEntitySet Field: " + e.getMessage());
		} catch (IllegalAccessException e) {
			ModLoader.outputError(e, "Failed to get trackedEntitySet instance: " + e.getMessage());
		}
	}

	@Override
	public String getCommandName() {
		return "dumptracked";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		int trackedEntities = 0;
		File dump = new File(new File("."), "trackeddump.txt");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < trackedEntitiesSet.length; i++) {
			sb.append("World ").append(i).append(":\n");
			Iterator trackedIterator = trackedEntitiesSet[i].iterator();
			while (trackedIterator.hasNext()) {
				trackedEntities++;
				Object obj = ((EntityTrackerEntry)trackedIterator.next()).trackedEntity;
				if (obj == null) {
					sb.append("Tracking null!\n");
				}
				else {
					sb.append("Tracking ").append(obj.getClass().getSimpleName());
					
					if (obj instanceof EntityItem) {
						sb.append(" (").append(((EntityItem)obj).item.getItemName()).append(")");
					}
					else {
						sb.append(" (").append(((Entity)obj).getEntityName()).append(")");
					}
					
					sb.append("\n");
				}
			}
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(dump));
			writer.write(sb.toString());
			writer.flush();
			writer.close();
			sender.sendChatToPlayer("Dumped " + trackedEntities + " tracked entities.");
		} catch (IOException e) {
			sender.sendChatToPlayer("Failed to dump tracked entities: " + e.getMessage());
		}
	}

}