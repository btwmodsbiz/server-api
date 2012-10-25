package btwmod.admincommands;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet102WindowClick;
import net.minecraft.src.Packet12PlayerLook;
import net.minecraft.src.Packet13PlayerLookMove;
import net.minecraft.src.Packet14BlockDig;
import net.minecraft.src.Packet15Place;
import net.minecraft.src.Packet3Chat;
import btwmods.CommandsAPI;
import btwmods.IMod;
import btwmods.NetworkAPI;
import btwmods.io.Settings;
import btwmods.network.IPacketListener;
import btwmods.network.PacketEvent;

public class mod_AdminCommands implements IMod, IPacketListener {
	
	private final Map<EntityPlayerMP, Long> lastPlayerAction = new HashMap<EntityPlayerMP, Long>();
	
	private WhoCommand whoCommand;
	private DumpTrackedCommand dumpTrackedCommand;

	@Override
	public String getName() throws Exception {
		return "Admin Commands";
	}

	@Override
	public void init(Settings settings) throws Exception {
		NetworkAPI.addListener(this);
		CommandsAPI.registerCommand(whoCommand = new WhoCommand(), this);
		CommandsAPI.registerCommand(dumpTrackedCommand = new DumpTrackedCommand(), this);
	}

	@Override
	public void unload() throws Exception {
		NetworkAPI.removeListener(this);
		CommandsAPI.unregisterCommand(whoCommand);
		CommandsAPI.unregisterCommand(dumpTrackedCommand);
	}

	@Override
	public IMod getMod() {
		return this;
	}

	@Override
	public void packetAction(PacketEvent event) {
		Packet packet = event.getPacket();
		if (packet instanceof Packet12PlayerLook
				|| packet instanceof Packet13PlayerLookMove
				|| packet instanceof Packet102WindowClick
				|| packet instanceof Packet14BlockDig
				|| packet instanceof Packet15Place
				|| packet instanceof Packet3Chat) {
			
			lastPlayerAction.put(event.getPlayer(), new Long(System.currentTimeMillis()));
		}
	}
}
