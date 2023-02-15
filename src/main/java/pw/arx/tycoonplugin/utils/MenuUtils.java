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
		
		if (!lore.isEmpty()) {
			meta.setLore(lore);
		}
		
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
	
	public static Inventory CreateTemplateInventory(String menuTitle) {
		Inventory inv = Bukkit.createInventory(null, TycoonMenuRows*9, menuTitle);
		
	    for (int i = 0; i < inv.getContents().length; i++) {
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
		Inventory inv = CreateTemplateInventory("Tycoon Upgrades");
		
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
	
	public static Inventory getBuildingUpgraderInventory(String building, Player p)
	{
		Config allBuildingsConfig = Tycoon.fileManager.getConfig("buildings.yml");
		ConfigurationSection buildingConfig = allBuildingsConfig.getConfigurationSection("buildings." + building);
		
		Config playerTycoonConfig = Tycoon.fileManager.getConfig("tycoons.yml");
		ConfigurationSection playerTycoonBuildingLevel = playerTycoonConfig.getConfigurationSection("tycoons." + p.getUniqueId().toString() + ".buildings." + building);
		
		int currentBuildingLevel = playerTycoonBuildingLevel.getInt("level");
		int maxBuildingLevel = buildingConfig.getList(".prices").size();
		int nextBuildingLevel = currentBuildingLevel+1;
		
		double currentMultiplier = Double.parseDouble(buildingConfig.getList(".multipliers").get(currentBuildingLevel-1).toString());
		String formattedMultiplier = String.format("%.2f", currentMultiplier);

		String upgraderTitle = allBuildingsConfig.get("upgrader_title").toString();

		Inventory inv = CreateTemplateInventory(upgraderTitle);
		
		ArrayList<String> LORE = new ArrayList<String>();

		if (currentBuildingLevel != maxBuildingLevel) {
			double nextUpgradeCost = Double.parseDouble(buildingConfig.getStringList("prices").get(currentBuildingLevel).toString());
			double nextUpgradeMultiplier = Double.parseDouble(buildingConfig.getList(".multipliers").get(currentBuildingLevel).toString());
			String formattedNextMultiplier = String.format("%.2f", nextUpgradeMultiplier);
			
			LORE.add(StringUtils.c("&e&lLevel " + nextBuildingLevel));
			LORE.add(StringUtils.c("&7" + formattedMultiplier + " &f> &e"+ formattedNextMultiplier));
			LORE.add(StringUtils.c("&a$" + String.format("%.2f", nextUpgradeCost)));
		} else {
			LORE.add(StringUtils.c("&7Fully Upgraded! " + maxBuildingLevel + "/" + maxBuildingLevel));
		}

		inv.setItem(13, GenItem("EMERALD_BLOCK", "&a&lUPGRADE " + building.toUpperCase(), LORE));
		return inv;
	}
	
	public static String getBuildingsMenuTitle() {
		Config INV_CONFIG = Tycoon.fileManager.getConfig("buildings.yml");
		return INV_CONFIG.get("title").toString(); 
	}

	public static Inventory LoadBuildingsMenu(Player p) {
		Config buildingsConfig = Tycoon.fileManager.getConfig("buildings.yml");

		Inventory inv = LoadInventoryFromConfig("buildings.yml");
		ConfigurationSection items = buildingsConfig.getConfigurationSection("buildings");

		for(String key : items.getKeys(false)) {

			boolean hasBuilding = BuildingManager.hasBuilding(p.getUniqueId().toString(), key);

			String MATERIAL = items.get(key + ".material").toString();
			String TITLE = items.get(key + ".title").toString();
			Integer SLOT = items.getInt(key + ".slot");

			ArrayList<String> LORE = new ArrayList<String>();

			for(String loreItem : (ArrayList<String>) items.getStringList(key + ".lore")) {
				LORE.add(StringUtils.c(loreItem));
			}

			if (hasBuilding == true) {
				String NEWTITLE = StringUtils.c("&c&l" + ChatColor.stripColor(StringUtils.c(TITLE)));
				ArrayList<String> NEWLORE = new ArrayList<String>();
				NEWLORE.add(StringUtils.c("&7Purchased"));
				inv.setItem(SLOT, GenItem(MATERIAL, NEWTITLE, NEWLORE));
			} else {
				inv.setItem(SLOT, GenItem(MATERIAL, TITLE, LORE, true));
			}
		}

		return inv;
	}

	public static Inventory LoadInventoryFromConfig(String name) {
		Config INV_CONFIG = Tycoon.fileManager.getConfig(name);
		
		Inventory inv = CreateTemplateInventory(getBuildingsMenuTitle());
		ConfigurationSection items = INV_CONFIG.getConfigurationSection("menu_items");
		
		for (String key : items.getKeys(false)) {
			String MATERIAL = items.get(key + ".material").toString();
			String TITLE = items.get(key + ".title").toString();
			int SLOT = items.getInt(key + ".slot");
			
			ArrayList<String> LORE = new ArrayList<String>();

			for (String loreItem : (ArrayList<String>) items.getStringList(key + ".lore")) {
				LORE.add(StringUtils.c(loreItem));
			}

			inv.setItem(SLOT, GenItem(MATERIAL, TITLE, LORE, true));
		}

		return inv;
	}
}
