package pw.arx.tycoonplugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pw.arx.tycoonplugin.Tycoon;
import pw.arx.tycoonplugin.managers.AdminManager;
import pw.arx.tycoonplugin.managers.TycoonManager;
import pw.arx.tycoonplugin.utils.*;

import java.util.ArrayList;

public class BlockBreakListener implements Listener {
	
	FileConfiguration config = Tycoon.getPlugin().getConfig();
	
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	
    	if(Tycoon.SELECTION_LOCATIONS.containsKey(event.getPlayer().getUniqueId())) {
    		Location SELECTED = Tycoon.SELECTION_LOCATIONS.get(event.getPlayer().getUniqueId());
    		ArrayList<Location> INSIDECUBELOC = LocUtils.selectOffset(SELECTED, Tycoon.SEL_OFFSET);
    		Boolean BLOCK_INSIDE = BlockUtils.insideCuboid(event.getBlock().getLocation(), INSIDECUBELOC.get(0), INSIDECUBELOC.get(1));
    		
    		if(BLOCK_INSIDE) {
    			event.setCancelled(true);
            	Bukkit.getScheduler().scheduleSyncDelayedTask(Tycoon.getPlugin(), new Runnable() {
                    public void run() {
                    	ArrayList<Location> NEW_LOC = LocUtils.selectOffset(SELECTED, Tycoon.SEL_OFFSET);
    		    		BlockUtils.blockFillFakes(event.getPlayer(), NEW_LOC.get(0), NEW_LOC.get(1), SELECTED);
                    }
                }, 3);
    			
    			event.getPlayer().sendMessage(StringUtils.c("&cDeselect this area to break blocks within this."));
    		}
    	}
    	
    	if(!AdminManager.isAdmin(event.getPlayer())) {
	    	if(TycoonManager.hasTycoon(event.getPlayer())) {
	    		if(WorldEditUtils.locationInsideTycoon(event.getBlock().getLocation())) {
	    			if(WorldEditUtils.playerInsideTheirRegion(event.getPlayer())) {
	        			event.getPlayer().sendMessage(StringUtils.c("&cYou can't break blocks inside your tycoon."));
	        			PlayerUtils.denySound(event.getPlayer());
	        			event.setCancelled(true);
	    			} else {
	        			event.getPlayer().sendMessage(StringUtils.c("&cYou can't break blocks inside someone elses tycoon."));
	        			PlayerUtils.denySound(event.getPlayer());
	        			event.setCancelled(true);    				
	    			}
	    		}
	    	}
    	}
    }
}