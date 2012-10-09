package btwmods.mods.spawnbeds;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.BlockBed;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EnumStatus;
import btwmods.api.player.IPlayerAPIMod;
import btwmods.api.player.PlayerAPI;
import btwmods.api.player.events.BlockEvent;
import btwmods.api.player.listeners.IBlockListener;

public class SpawnBeds implements IPlayerAPIMod, IBlockListener {
	
	private PlayerAPI api;

	@Override
	public void init(PlayerAPI parent) {
		api = parent;
		parent.addListener(this);
	}

	@Override
	public void unload(PlayerAPI parent) {
		api = parent;
		parent.removeListener(this);
	}

	@Override
	public void blockActivated(BlockEvent event) {
		api.player.sendChatToPlayer("sheeet");
		if (event.getBlock() instanceof BlockBed && !api.player.worldObj.isRemote) {
			
			int metadata = event.getMetadata();
			int x = event.getX();
			int y = event.getY();
			int z = event.getZ();

			api.player.sendChatToPlayer(event.getWorld().provider.getClass().toString());
			
			if (!BlockBed.isBlockHeadOfBed(metadata)) {
                int var11 = BlockBed.getDirection(metadata);
                x += BlockBed.footBlockToHeadBlockMap[var11][0];
                z += BlockBed.footBlockToHeadBlockMap[var11][1];

                if (event.getWorld().getBlockId(x, y, z) != event.getBlock().blockID)
                {
                    return;
                }

                metadata = event.getWorld().getBlockMetadata(x, y, z);
            }
			
			if (!event.getWorld().provider.canRespawnHere()) {
                double var19 = (double)x + 0.5D;
                double var21 = (double)y + 0.5D;
                double var15 = (double)z + 0.5D;
                event.getWorld().setBlockWithNotify(x, y, z, 0);
                int var17 = BlockBed.getDirection(metadata);
                x += BlockBed.footBlockToHeadBlockMap[var17][0];
                z += BlockBed.footBlockToHeadBlockMap[var17][1];

                if (event.getWorld().getBlockId(x, y, z) == event.getBlock().blockID)
                {
                	event.getWorld().setBlockWithNotify(x, y, z, 0);
                    var19 = (var19 + (double)x + 0.5D) / 2.0D;
                    var21 = (var21 + (double)y + 0.5D) / 2.0D;
                    var15 = (var15 + (double)z + 0.5D) / 2.0D;
                }

                event.getWorld().newExplosion((Entity)null, (double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), 5.0F, true);
                return;
            }
			
            if (api.player.isPlayerSleeping() || !api.player.isEntityAlive()) {
                return; // EnumStatus.OTHER_PROBLEM;
            }

            if (!api.player.worldObj.provider.isSurfaceWorld()) {
                return; // EnumStatus.NOT_POSSIBLE_HERE;
            }

            if (api.player.worldObj.isDaytime()) {
    			api.player.sendChatToPlayer("... it would be better to sleep at night.");
                return; // EnumStatus.NOT_POSSIBLE_NOW;
            }

            if (Math.abs(api.player.posX - (double)event.getX()) > 3.0D || Math.abs(api.player.posY - (double)event.getY()) > 2.0D || Math.abs(api.player.posZ - (double)event.getZ()) > 3.0D) {
    			api.player.sendChatToPlayer("... and that bed is too far away.");
                return; // EnumStatus.TOO_FAR_AWAY;
            }

            double var4 = 8.0D;
            double var6 = 5.0D;
            List var8 = api.player.worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)event.getX() - var4, (double)event.getY() - var6, (double)event.getZ() - var4, (double)event.getX() + var4, (double)event.getY() + var6, (double)event.getZ() + var4));

            if (!var8.isEmpty()) {
            	api.player.addChatMessage("tile.bed.notSafe");
    			api.player.sendChatToPlayer("... there are monsters nearby!");
                return; // EnumStatus.NOT_SAFE;
            }

			api.player.setSpawnChunk(new ChunkCoordinates(event.getX(), event.getY(), event.getZ()));
			api.player.sendChatToPlayer("... but you feel as if this is your new home.");

	        //return EnumStatus.OK;
		}
	}

}
