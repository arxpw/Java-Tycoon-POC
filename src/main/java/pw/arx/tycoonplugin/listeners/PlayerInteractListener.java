package pw.arx.tycoonplugin.listeners;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.line.HologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import pw.arx.tycoonplugin.Tycoon;
import pw.arx.tycoonplugin.commands.Create;
import pw.arx.tycoonplugin.managers.BuildingManager;
import pw.arx.tycoonplugin.managers.TycoonManager;
import pw.arx.tycoonplugin.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class PlayerInteractListener implements Listener {
	
	FileConfiguration config = Tycoon.getPlugin().getConfig();

	Action[] ListenerActions = { 
		Action.RIGHT_CLICK_BLOCK,
		Action.RIGHT_CLICK_AIR,
		Action.LEFT_CLICK_BLOCK,
		Action.LEFT_CLICK_AIR,
	};
	
	Action[] SelectorActionClear = {
		ListenerActions[0],
		ListenerActions[1]
	};
	
	Action[] SelectorActionCreate = {
		ListenerActions[2],
		ListenerActions[3]
	};
	
	Cooldown cratedown = new Cooldown(2);
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent event)
	{
    	if (!Arrays.stream(ListenerActions).anyMatch(event.getAction()::equals)) {
			return;
		}
	    
       	EquipmentSlot e = event.getHand();
    	if (!e.equals(EquipmentSlot.HAND)) {
			return;
		}
    	
	    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	    	
	    	if(event.getClickedBlock().getType().equals(Material.PLAYER_HEAD)) {
	        	if(TycoonManager.hasTycoon(event.getPlayer())) {
	        		if(WorldEditUtils.playerInsideTheirRegion(event.getPlayer())) {
	        			
	        			for(Hologram holo : HolographicDisplaysAPI.get(Tycoon.getPlugin()).getHolograms()) {
							String worldName = holo.getPosition().getWorldName();
							Block block = event.getClickedBlock();

							String blockWorldName = block.getWorld().getName();

	        				if(blockWorldName.equals(worldName)) {
	        					if(holo.getPosition().distance(block.getLocation()) < 2) {
	        						HologramLine tl = holo.getLines().get(0);
	        						TextHologramLine ctl = (TextHologramLine) tl;

	        						boolean buyer = ctl.getText().contains("BUILDING");
	        						
	        						if (buyer) {
										event.getPlayer().openInventory(MenuUtils.LoadMenuFromConfig("buildings", event.getPlayer()));
									}
	        					}
	        				}
	        			}
	        		} else {
	        			event.getPlayer().sendMessage("This isn't your tycoon!");
	        		}
	        	}
	        	return;
	    	}
	    	
	    	if(event.getClickedBlock().getType().equals(Material.BARREL)) {
	    		String activeBlock = TycoonManager.getBuildingByLocation(event.getPlayer(), event.getClickedBlock().getLocation().add(0,1,0));

	    		if(activeBlock != null) {
	    			event.setCancelled(true);
	    			event.getPlayer().openInventory(MenuUtils.buildingUpgrader(activeBlock, event.getPlayer()));
	    		}
	    	}
	    }
	    
	    if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
	    	if(event.getClickedBlock().getType().equals(Material.BARREL)) {
	    		String activeBlock = TycoonManager.getBuildingByLocation(event.getPlayer(), event.getClickedBlock().getLocation().add(0,1,0));

	    		if(activeBlock.contains("crate")) {
	    			if(cratedown.isOnCooldown(event.getPlayer()) == false) {
	    				BuildingManager.buildingClicker(activeBlock, event.getPlayer());
	    				cratedown.setCooldown(event.getPlayer());
	    			}
	    		}
	    	}
	    }

    	if (!event.getPlayer().getItemInHand().equals(ItemUtils.LandSelector(true))) {
			return;
		}

		Player p = event.getPlayer();
		Block tblock = BlockUtils.targetBlock(p);
		event.setCancelled(true);
		
		// create actions, already selected?
		if(Arrays.stream(SelectorActionCreate).anyMatch(event.getAction()::equals)) {
			
			if(TycoonManager.hasTycoon(p)) {
				p.sendMessage(StringUtils.c("&cYou already have a tycoon! Delete this before making a new one with /tycoon delete"));
				return;				
			}
			
			if(TycoonManager.canPlaceTycoon(tblock.getLocation()) == false) {
				p.sendMessage(StringUtils.c("&cAnother Tycoon is already too close!"));
				return;
			}
			
			// does the selection contain a player?
			if(Tycoon.SELECTION_LOCATIONS.containsKey(event.getPlayer().getUniqueId())) {
				
				Location SELECTED = Tycoon.SELECTION_LOCATIONS.get(p.getUniqueId());
				ArrayList<Location> INSIDECUBELOC = LocUtils.selectOffset(SELECTED, Tycoon.SEL_OFFSET);
				Boolean BLOCK_INSIDE = BlockUtils.insideCuboid(tblock.getLocation(), INSIDECUBELOC.get(0), INSIDECUBELOC.get(1));
				
				if(BLOCK_INSIDE) {
					// is the player updating a block on their client side?
					// will need to update this back to the selection.
					
                	Bukkit.getScheduler().scheduleSyncDelayedTask(Tycoon.getPlugin(), new Runnable() {
                        public void run() {
        					if(tblock.equals(SELECTED.getBlock())) {
        						p.sendBlockChange(tblock.getLocation(), Material.GOLD_BLOCK.createBlockData());
        					}
                        }
                    }, 3);

					if(tblock.equals(SELECTED.getBlock())) {
						Create.Command(p);
					} else {
						p.sendMessage(StringUtils.c("&cArea already selected!"));
					}
					return;
				}
			}			
		}
		
    	HashMap<UUID, Location> SEL_HASHMAP = Tycoon.SELECTION_LOCATIONS;
        
    	// clear
    	if (Arrays.stream(SelectorActionClear).anyMatch(event.getAction()::equals)) {
			if (SEL_HASHMAP.containsKey(p.getUniqueId())) {
				// UPDATE BLOCKS...                
                if (TycoonSelector.ClearSelection(p)) {
                	p.sendMessage(StringUtils.c("Land selection &bcleared&r!"));
                }
			} else {
				p.sendMessage(StringUtils.c("&cYou don't have any land selected!"));
			}
    	} 
    	
    	// right clicking / create selection
    	if(Arrays.stream(SelectorActionCreate).anyMatch(event.getAction()::equals)) {

    		// player has an existing selection?
    		if (SEL_HASHMAP.containsKey(p.getUniqueId())) {
				// UPDATE OLD BLOCKS...
                ArrayList<Location> OLD_SELECTION = LocUtils.selectOffset(SEL_HASHMAP.get(p.getUniqueId()), Tycoon.SEL_OFFSET);
                BlockUtils.blockUpdate(OLD_SELECTION.get(0), OLD_SELECTION.get(1), p);
			}

			TycoonSelector.SelectBlocks(p);
		}
    }
}
