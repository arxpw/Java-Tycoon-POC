package pw.arx.tycoonplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pw.arx.tycoonplugin.Tycoon;
import pw.arx.tycoonplugin.managers.BuildingManager;
import pw.arx.tycoonplugin.managers.FileManager.Config;

import java.util.ArrayList;

public class MenuUtils {
			
	public static String centerTitle(String title) {
	    String spacer = "";
	    int spaces = 27 - ChatColor.stripColor(title).length();
	    for (int i = 0; i < spaces; i++) {
	        spacer += " ";
	    }
	    return spacer + title;
	}
	
	public static Integer TycoonMenuRows = 3;
	
	public static ItemStack GenItem(String item, String title, ArrayList<String> lore) {
		Material mat = Material.getMaterial(item);
		
		ItemStack return_stack = new ItemStack(mat);
		ItemMeta meta = return_stack.getItemMeta();
		
		if(!lore.isEmpty() && lore.size() > 0)
			meta.setLore(lore);
		
		meta.setDisplayName(StringUtils.c(title));
		
		return_stack.setItemMeta(meta);
		return return_stack;
	}
	
	public static ItemStack GenItem(String item, String title, ArrayList<String> lore, Boolean enchanted) {
		Material mat = Material.getMaterial(item);
		
		ItemStack return_stack = new ItemStack(mat);
		ItemMeta meta = return_stack.getItemMeta();
		
		if(!lore.isEmpty() && lore.size() > 0)
			meta.setLore(lore);
		
		meta.setDisplayName(StringUtils.c(title));
		meta.addEnchant(Enchantment.DURABILITY, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		return_stack.setItemMeta(meta);
		return return_stack;
	}
	
	public static ItemStack GenItem(String item, String title) {
		ArrayList<String> lore = new ArrayList<String>();
		return GenItem(item,title,lore);
	}
	
	public static Inventory FILLBG(String menuTitle) {
		Inventory inv = Bukkit.createInventory(null, TycoonMenuRows*9, menuTitle);
		
	    for(int i = 0; i < inv.getContents().length; i++) {
	    	inv.setItem(i, GenItem("BLACK_STAINED_GLASS_PANE"," "));
	    }
	    
	    return inv;
	}
	
	public static ArrayList<String> SingleLoreArray(String st) {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(StringUtils.c(st));
		return lore;
	}
	
	public static Inventory TycoonMenu() {
		Inventory inv = FILLBG("GREG");
		
		inv.setItem(10, GenItem("BRICKS", "&c&lTycoon Buildings", SingleLoreArray("&7/tycoon buildings")));
		inv.setItem(12, GenItem("GOLD_BLOCK", "&6&lTycoon General", SingleLoreArray("&7/tycoon general")));
		inv.setItem(14, GenItem("IRON_BLOCK", "&b&lTycoon Upgrades", SingleLoreArray("&7/tycoon upgrades")));
		inv.setItem(16, GenItem("PAPER", "&3&lTycoon Info", SingleLoreArray("&7/tycoon info")));
		
		return inv;
	}
	
	
	public static String getBuildingsUpgraderMenuTitle() {
		Config INV_CONFIG = Tycoon.fileManager.getConfig("buildings.yml");
		return INV_CONFIG.get("upgrader_title").toString(); 
	}
	
	public static Inventory buildingUpgrader(String building, Player p) {
		
		Config BUILDINGS_CONFIG = Tycoon.fileManager.getConfig("buildings.yml");
		ConfigurationSection this_building = BUILDINGS_CONFIG.getConfigurationSection("buildings." + building);
		
		Config PL_BUILDING_CONFIG = Tycoon.fileManager.getConfig("tycoons.yml");
		ConfigurationSection player_building = PL_BUILDING_CONFIG.getConfigurationSection("tycoons." + p.getUniqueId().toString() + ".buildings." + building);
		
		Integer CURRENT_LEVEL = player_building.getInt("level");
		Integer MAX_LEVEL = this_building.getList(".prices").size();
		Integer NEXT_LEVEL = CURRENT_LEVEL+1;
		
		Double THIS_MULTIPLIER = Double.parseDouble(this_building.getList(".multipliers").get(CURRENT_LEVEL-1).toString());		
		String formattedMultiplier = String.format("%.2f", THIS_MULTIPLIER);

		
		Inventory inv = FILLBG(getBuildingsUpgraderMenuTitle());
		
		ArrayList<String> LORE = new ArrayList<String>();
		
		
		if(CURRENT_LEVEL != MAX_LEVEL) {
			Double NEXT_COST = Double.parseDouble(this_building.getStringList("prices").get(CURRENT_LEVEL).toString());
			Double NEXT_MULTIPLIER = Double.parseDouble(this_building.getList(".multipliers").get(CURRENT_LEVEL).toString());
			String formattedNextMultiplier = String.format("%.2f", NEXT_MULTIPLIER);
			
			LORE.add(StringUtils.c("&e&lLevel " + NEXT_LEVEL));
			LORE.add(StringUtils.c("&7" + formattedMultiplier + " &f> &e"+ formattedNextMultiplier));
			LORE.add(StringUtils.c("&a$" + String.format("%.2f", NEXT_COST)));
						
		} else {
			LORE.add(StringUtils.c("&7Fully Upgraded! " + MAX_LEVEL + "/" + MAX_LEVEL));
		}
		
		
		inv.setItem(13, GenItem("EMERALD_BLOCK", "&a&lUPGRADE " + building.toUpperCase(), LORE));
		return inv;
	}
	
	public static String getBuildingsMenuTitle() {
		Config INV_CONFIG = Tycoon.fileManager.getConfig("buildings.yml");
		return INV_CONFIG.get("title").toString(); 
	}
	
	public static Inventory LoadMenuFromConfig(String name, Player p) {
		Config INV_CONFIG = Tycoon.fileManager.getConfig("buildings.yml");
		
		Inventory inv = FILLBG(getBuildingsMenuTitle());
		ConfigurationSection items = INV_CONFIG.getConfigurationSection("buildings");
		
		for(String key : items.getKeys(false)) {
		
			boolean hasBuilding = BuildingManager.hasBuilding(p.getUniqueId().toString(), key);
			
			String MATERIAL = items.get(key + ".material").toString();
			String TITLE = items.get(key + ".title").toString();
			Integer SLOT = items.getInt(key + ".slot");
			
			ArrayList<String> LORE = new ArrayList<String>(); 
			for(String loreItem : (ArrayList<String>) items.getStringList(key + ".lore")) {
				LORE.add(StringUtils.c(loreItem));
			}
			
			if(hasBuilding == true) {
//				Bukkit.broadcastMessage("hasbuilding: " + TITLE);
				String NEWTITLE = StringUtils.c("&c&l" + ChatColor.stripColor(StringUtils.c(TITLE)));
				ArrayList<String> NEWLORE = new ArrayList<String>(); 
				NEWLORE.add(StringUtils.c("&7Purchased"));
				inv.setItem(SLOT, GenItem(MATERIAL, NEWTITLE, NEWLORE));
			} else {
//				Bukkit.broadcastMessage("does not have building: " + TITLE);
				inv.setItem(SLOT, GenItem(MATERIAL, TITLE, LORE, true));
			}

		}

		return inv;
	}

}
