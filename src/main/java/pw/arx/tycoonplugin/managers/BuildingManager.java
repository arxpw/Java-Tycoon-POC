package pw.arx.tycoonplugin.managers;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pw.arx.tycoonplugin.Tycoon;
import pw.arx.tycoonplugin.managers.FileManager.Config;
import pw.arx.tycoonplugin.utils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BuildingManager {
	public static Boolean hasBuilding(String UUID, String building) {
		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");
		ConfigurationSection t = tycoons.getConfigurationSection("tycoons");
		
		if(t.isSet(UUID + ".buildings." + building + ".level"))
			return true;
		
		return false;
	}
	
	public static Location getBuildingBlockLocation(Player p, String building) {
		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");
		String locString = "tycoons." + p.getUniqueId().toString() + ".clickers." + building + ".location";
		
		Location l = new Location(
			Bukkit.getWorld(tycoons.get("tycoons." + p.getUniqueId().toString() + ".location.world").toString()),
			Double.parseDouble(tycoons.get(locString + ".x").toString()),
			Double.parseDouble(tycoons.get(locString + ".y").toString()),
			Double.parseDouble(tycoons.get(locString + ".z").toString())
		);
		
		return l;
	}
	
	public static ConfigurationSection getBuildingConfig(String building) {
		Config buildings = Tycoon.fileManager.getConfig("buildings.yml");
		ConfigurationSection bld = buildings.getConfigurationSection("buildings");

		return bld.getConfigurationSection("." + building); 
	}
	
	public static void upgradeBuilding(String building, Player p) {
		
		ConfigurationSection BUILDING = BuildingManager.getBuildingConfig(building);
		
		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");
		String TYCOON_CFG_STRING = "tycoons." + p.getUniqueId().toString();
		
		ConfigurationSection CURRENT_PL_BUILDING = tycoons.getConfigurationSection(TYCOON_CFG_STRING + ".buildings." + building);
		
		int MAX_LEVEL = BUILDING.getList(".prices").size();
		int CURRENT_LEVEL = CURRENT_PL_BUILDING.getInt("level");
		int NEXT_LEVEL = CURRENT_LEVEL+1;
		
		if(MAX_LEVEL == CURRENT_LEVEL ) {
			p.sendMessage("Level " + MAX_LEVEL + " is the highest you can upgrade to.");
			// p.sendMessage("Multiplier #1" + BUILDING.getList(".multipliers").get(CURRENT_LEVEL).toString());
			return;
		}
		
		double NEXT_MULTIPLIER = Double.parseDouble(BUILDING.getList(".multipliers").get(CURRENT_LEVEL).toString());
		double COST = Double.parseDouble(BUILDING.getList(".prices").get(CURRENT_LEVEL).toString());
		double PL_BALANCE = Tycoon.getEconomy().getBalance(p);
		
		if(PL_BALANCE - COST >= 0) {
			
			Tycoon.getEconomy().withdrawPlayer(p, COST);
			
			tycoons.set(TYCOON_CFG_STRING + ".buildings." + building + ".level", NEXT_LEVEL);
			tycoons.set(TYCOON_CFG_STRING + ".buildings." + building + ".multiplier", NEXT_MULTIPLIER);
			
			List<String> BUILDING_LINES = tycoons.getConfigurationSection(TYCOON_CFG_STRING + ".clickers." + building).getStringList("lines");
			// set "level 1" to level #blah
			BUILDING_LINES.set(1, BUILDING_LINES.get(1).substring(0, 8) + (NEXT_LEVEL));
			
			if(building.contains("crate")) {
				BUILDING_LINES.set(2, "&a$" + String.format("%.2f", NEXT_MULTIPLIER) + " &rper click.");
			} else {
				BUILDING_LINES.set(2, "&a$" + String.format("%.2f", NEXT_MULTIPLIER) + " &rper minute.");
			}

			tycoons.set(TYCOON_CFG_STRING + ".clickers." + building + ".lines", BUILDING_LINES);
			tycoons.save();
			
			if (CURRENT_LEVEL + 1 == MAX_LEVEL) {
				EffectUtils.spawnFireworks(p.getLocation().add(0,8,0), 5);
			}
			
			p.sendMessage(StringUtils.c("&7" + BUILDING_LINES.get(0).toString() + " &7Upgraded to &eLevel " + NEXT_LEVEL));
			p.openInventory(MenuUtils.buildingUpgrader(building, p));
			
			BlockUtils.loadHolograms();
			
		} else {
			p.sendMessage(StringUtils.c("You cannot afford to upgrade this. &c$" + PL_BALANCE + "&7/&a$" + COST ));
		}
		
	}
	
	public static void buildingClicker(String buildingName, Player p) {
		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");
		String TYCOON_CFG_STRING = "tycoons." + p.getUniqueId().toString();
		
		ConfigurationSection THISTYCOON = tycoons.getConfigurationSection(TYCOON_CFG_STRING);
		ConfigurationSection THISBUILDING = THISTYCOON.getConfigurationSection(".buildings." + buildingName);
		
		Double AMOUNT = THISBUILDING.getDouble("multiplier");
		Tycoon.getEconomy().depositPlayer(p, AMOUNT);
		p.sendMessage(StringUtils.c("&a+ $" + AMOUNT + " &rfrom &e" + buildingName + "&7, total: &a$" + Tycoon.getEconomy().getBalance(p)));
		
	}

	public static void buyBuilding(String building, Player p) {
		ConfigurationSection BUILDING = BuildingManager.getBuildingConfig(building);

		double COST = Double.parseDouble(BUILDING.getList(".prices").get(0).toString());
		double PL_BALANCE = Tycoon.getEconomy().getBalance(p);
		
		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");
		String TYCOON_CFG_STRING = "tycoons." + p.getUniqueId().toString();
		
		ConfigurationSection THISTYCOON = tycoons.getConfigurationSection(TYCOON_CFG_STRING);
		ConfigurationSection THISCLICKER = THISTYCOON.getConfigurationSection(".clickers." + building);
		
		Location LOC_OFFSET = new Location(Bukkit.getWorld(
			THISTYCOON.getString(".location.world")),
			THISCLICKER.getDouble(".location.x"),
			THISCLICKER.getDouble(".location.y"),
			THISCLICKER.getDouble(".location.z")
		);

		Location LOCATION_FIXED = new Location( LOC_OFFSET.getWorld(), LOC_OFFSET.getX(), LOC_OFFSET.getY(), LOC_OFFSET.getZ() );
		
		if (PL_BALANCE - COST >= 0) {
			// close when purchased
			p.getOpenInventory().close();
			p.sendMessage(StringUtils.c("&e" + StringUtils.cap(building) + " &rPurchased!"));
			Tycoon.getEconomy().withdrawPlayer(p, COST);
			
			WorldEditUtils.loadSchematic(
				TycoonManager.getTycoonLocation(p.getUniqueId().toString()),
				TycoonManager.getTycoonDirection(p.getUniqueId().toString()),
				"tycoon1" + building
			);

			LOC_OFFSET.getBlock().setType(Material.BARREL);
			
			LOC_OFFSET.add(Tycoon.HOLOGRAM_OFFSET);
			LOCATION_FIXED.add(0,1,0);

			Plugin plugin = Tycoon.getPlugin(); // Your plugin's instance
			HolographicDisplaysAPI api = HolographicDisplaysAPI.get(plugin); // The API instance for your plugin

			for(Hologram h : api.getHolograms()) {
				if (h.getPosition().toLocation().distance(LOC_OFFSET) < 1) {
					h.getLines().clear();
					
					double FIRST_MULTIPLIER = Double.parseDouble(BUILDING.getList(".multipliers").get(0).toString());
					String MULTIPLIER_FORMATTED = String.format("%.2f", FIRST_MULTIPLIER);
					
	        		List<String> LINES = new ArrayList<>();
					
	        		// crates can be clicked..
					if(building.contains("crate")) {
		        		LINES.add("&6&l" + building.toUpperCase());
		        		LINES.add("&rLEVEL 1");
		        		LINES.add("&a$" + MULTIPLIER_FORMATTED + " &rper click.");
						
						h.getLines().appendText(StringUtils.c(LINES.get(0)));
						h.getLines().appendText(StringUtils.c(LINES.get(1)));
						h.getLines().appendText(StringUtils.c(LINES.get(2)));
						
						h.setPosition(LOC_OFFSET.add(0,1,0));
						
						tycoons.set(TYCOON_CFG_STRING + ".clickers." + building + ".location.y", LOCATION_FIXED.getY());
						tycoons.set(TYCOON_CFG_STRING + ".clickers." + building + ".lines", LINES);
						tycoons.set(TYCOON_CFG_STRING + ".buildings." + building + ".level", 1);
						tycoons.set(TYCOON_CFG_STRING + ".buildings." + building + ".multiplier", FIRST_MULTIPLIER);
						
						tycoons.save();
						continue;
					}
					
					// Double FIRST_PRICE = Double.parseDouble(BUILDING.getList(".prices").get(0).toString());
	        		LINES.add("&6&l" + building.toUpperCase());
	        		LINES.add("&rLEVEL 1");
	        		LINES.add("&a$" + MULTIPLIER_FORMATTED + " &rper minute.");

					h.getLines().appendText(StringUtils.c(LINES.get(0)));
					h.getLines().appendText(StringUtils.c(LINES.get(1)));
					h.getLines().appendText(StringUtils.c(LINES.get(2)));

					h.setPosition(LOC_OFFSET.add(0,1,0));
					
					tycoons.set(TYCOON_CFG_STRING + ".clickers." + building + ".location.y", LOCATION_FIXED.getY());
					tycoons.set(TYCOON_CFG_STRING + ".clickers." + building + ".lines", LINES);
					tycoons.set(TYCOON_CFG_STRING + ".buildings." + building + ".level", 1);
					tycoons.set(TYCOON_CFG_STRING + ".buildings." + building + ".multiplier", FIRST_MULTIPLIER);
					
					tycoons.save();
				}
			}
					
		} else {
			p.sendMessage(StringUtils.c("You can't afford this building! &c$" + PL_BALANCE + "&7/&a$" + COST));
		}
	}
}
