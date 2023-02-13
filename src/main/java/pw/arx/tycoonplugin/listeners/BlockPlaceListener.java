package pw.arx.tycoonplugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import pw.arx.tycoonplugin.managers.AdminManager;
import pw.arx.tycoonplugin.managers.TycoonManager;
import pw.arx.tycoonplugin.utils.PlayerUtils;
import pw.arx.tycoonplugin.utils.StringUtils;
import pw.arx.tycoonplugin.utils.WorldEditUtils;

public class BlockPlaceListener implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {

		if(!AdminManager.isAdmin(event.getPlayer())) {
	    	if(TycoonManager.hasTycoon(event.getPlayer())) {
	    		if(WorldEditUtils.locationInsideTycoon(event.getBlock().getLocation())) {
	        		if(WorldEditUtils.playerInsideTheirRegion(event.getPlayer())) {
	        			event.getPlayer().sendMessage(StringUtils.c("&cYou can't place blocks inside your tycoon."));
	        			PlayerUtils.denySound(event.getPlayer());
	        			event.setCancelled(true);
	        		} else {
	        			event.getPlayer().sendMessage(StringUtils.c("&cYou can't place blocks inside someone elses tycoon."));
	        			PlayerUtils.denySound(event.getPlayer());
	        			event.setCancelled(true);        			
	        		}
	    		}
	    	}
		}
		
	}
}
