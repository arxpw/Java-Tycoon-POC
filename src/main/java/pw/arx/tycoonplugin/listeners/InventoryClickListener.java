package pw.arx.tycoonplugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.meta.ItemMeta;
import pw.arx.tycoonplugin.managers.BuildingManager;
import pw.arx.tycoonplugin.utils.MenuUtils;
import pw.arx.tycoonplugin.utils.StringUtils;

public class InventoryClickListener implements Listener {
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		InventoryView iv = p.getOpenInventory();
		
		if(iv.getTitle() == MenuUtils.getBuildingsUpgraderMenuTitle()) {
			e.setCancelled(true);
			
			if(e.getCurrentItem().getType().equals(Material.BLACK_STAINED_GLASS_PANE))
				return;
			
			if(e.getCurrentItem().getType().equals(Material.EMERALD_BLOCK)) {
				
//				String activeBlock = TycoonManager.getBuildingByLocation(p, BlockUtils.targetBlock(p).getLocation().add(0,1,0) );
				ItemMeta IM = e.getCurrentItem().getItemMeta();
				String NAMESTRIPPED = ChatColor.stripColor(IM.getDisplayName());
				String BUILDINGNAME = NAMESTRIPPED.substring(8).toLowerCase();
				BuildingManager.upgradeBuilding(BUILDINGNAME, p);
			}
			
		}
		
		if(iv.getTitle().equals(MenuUtils.getBuildingsMenuTitle())) {
			e.setCancelled(true);
			
			if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.BLACK_STAINED_GLASS_PANE))
				return;
			
			// we're in the buildings menu and clicking an item..
			if(e.getCurrentItem().getType().equals(Material.PUMPKIN)) {
				if(e.getCurrentItem().containsEnchantment(Enchantment.DURABILITY)) {
					BuildingManager.buyBuilding("farm", p);
					return;
				} else {
					p.sendMessage(StringUtils.c("&cYou already own this!"));
				}
			}
			
			// we're in the buildings menu and clicking an item..
			if(e.getCurrentItem().getType().equals(Material.EMERALD_ORE)) {
				if(e.getCurrentItem().containsEnchantment(Enchantment.DURABILITY)) {
					BuildingManager.buyBuilding("mineshaft", p);
					return;
				} else {
					p.sendMessage(StringUtils.c("&cYou already own this!"));
				}
			}
			
			// we're in the buildings menu and clicking an item..
			if(e.getCurrentItem().getType().equals(Material.HAY_BLOCK)) {
				if(e.getCurrentItem().containsEnchantment(Enchantment.DURABILITY)) {
					BuildingManager.buyBuilding("farmcrate", p);
					return;
				} else {
					p.sendMessage(StringUtils.c("&cYou already own this!"));
				}
			}
			
		}
		return;
	}
}