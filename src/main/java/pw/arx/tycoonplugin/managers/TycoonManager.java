package pw.arx.tycoonplugin.managers;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pw.arx.tycoonplugin.Tycoon;
import pw.arx.tycoonplugin.managers.FileManager.Config;
import pw.arx.tycoonplugin.utils.*;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class TycoonManager {
	
	public static Boolean hasTycoon(Player p) {
		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");
		Boolean exists = tycoons.isConfigurationSection("tycoons." + p.getUniqueId().toString());
		return exists;
	}
	
	public static ConfigurationSection getTycoonBuildings(Player p) {
		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");
		ConfigurationSection keys = tycoons.getConfigurationSection("tycoons." + p.getUniqueId().toString() + ".buildings");
		return keys;
	}

	public static void addTycoonGains() {
		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");

		for(String tycoon_single : tycoons.getConfigurationSection("tycoons").getKeys(false)) {
			ConfigurationSection buildings = tycoons.getConfigurationSection("tycoons." + tycoon_single + ".buildings");
			double totalToAdd = 0.00;

			if (buildings == null) {
				return;
			}

			for(String building : buildings.getKeys(true)) {
				if (building.contains("crate")) {
					continue;
				}

				ConfigurationSection cfgs = TycoonManager.getTycoon(tycoon_single);
				double multiplier = cfgs.getDouble("buildings." + building + ".multiplier");

				if (multiplier > 0) {
					totalToAdd = totalToAdd + multiplier;
				}
			}

			for(Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
				// let's not spam players who don't have a tycoon
				if (!onlinePlayer.getUniqueId().toString().equals(tycoon_single)) {
					continue;
				}

				// let's not spam players who don't earn any money
				if(totalToAdd > 0) {
					Tycoon.getEconomy().depositPlayer(onlinePlayer, totalToAdd);
					onlinePlayer.sendMessage(StringUtils.c("&rYour tycoon has generated &a$" + totalToAdd + "!"));
				}
			}
		}
	}
	
	public static String getBuildingByLocation(Player p, Location l) {
		ConfigurationSection player = getTycoonBuildings(p);
		for(String build : player.getKeys(true)) {
			// we only want existing buildings
			if(!player.isBoolean(build)) {
				ConfigurationSection cs = TycoonManager.getTycoon(p.getUniqueId().toString());
				Location clickerLocation = new Location(
						Bukkit.getWorld(cs.get("location.world").toString()),
						cs.getDouble("clickers." + build + ".location.x"),
						cs.getDouble("clickers." + build + ".location.y"),
						cs.getDouble("clickers." + build + ".location.z")
				);
				
				if(l.distance(clickerLocation) < 1) {
					return build;
				}
			}
		}

		return null;
	}

	public static void spawnTycoon(Location l, Player p) throws FileNotFoundException {
		String UUID = p.getUniqueId().toString();
		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");
		ConfigurationSection t = tycoons.getConfigurationSection("tycoons");
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		t.set(UUID + ".created", dateFormat.format(date));
		t.set(UUID + ".location.direction", LocUtils.schematicRotateAmount(p));
		t.set(UUID + ".location.x", l.getX());
		t.set(UUID + ".location.y", l.getY());
		t.set(UUID + ".location.z", l.getZ());
		t.set(UUID + ".location.world", l.getWorld().getName());
		
		t.set(UUID + ".buildings.farm", false);
		t.set(UUID + ".buildings.mineshaft", false);
		t.set(UUID + ".buildings.farmcrate", false);
		
		tycoons.save();
		
		WorldEditUtils.loadTycoonSchematic(l, p, "tycoon1");
		WorldEditUtils.regionCreate(l, p);
		
    	ArrayList<Location> LOC_ARRAY = LocUtils.selectOffset(l, Tycoon.SEL_OFFSET);
    	ArrayList<Location> LOC_GROUND = LocUtils.selectOffset(l, Tycoon.SEL_OFFSET);
    	ArrayList<Location> LOC_SCHEMATIC = LocUtils.selectOffset(l, Tycoon.SEL_OFFSET);
    	LOC_SCHEMATIC.get(1).add(0, 11, 0);

    	Bukkit.getScheduler().scheduleSyncDelayedTask(Tycoon.getPlugin(), new Runnable() {
			@Override
			public void run() {
				BlockUtils.blockUpdate(LOC_ARRAY.get(0), LOC_ARRAY.get(1), p);
				BlockUtils.blockOutline(Material.GREEN_TERRACOTTA, LOC_GROUND.get(0), LOC_GROUND.get(1));
				BlockUtils.signBuyers(p, LOC_SCHEMATIC.get(0), LOC_SCHEMATIC.get(1));

				TycoonSelector.ClearSelection(p);

				p.sendMessage(StringUtils.c("&aTycoon Created!"));
			}
		}, 10L);
	}
	
	public static void deleteTycoon(Player p) {
		Location blockLocation = getTycoonLocation(p.getUniqueId().toString());
		
		String UUID = p.getUniqueId().toString();
		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");
		ConfigurationSection t = tycoons.getConfigurationSection("tycoons");

		t.set(UUID, null);
		tycoons.save();
		
		// clear region
		WorldEditUtils.regionDelete(p);
		
		// clear build
		ArrayList<Location> OUTLINE = LocUtils.selectOffset(blockLocation, Tycoon.SEL_OFFSET);
		
		BlockUtils.blockFill(Material.AIR, OUTLINE.get(0).add(0,1,0), OUTLINE.get(1).add(0, 20, 0));
		BlockUtils.blockFill(Material.GRASS_BLOCK, OUTLINE.get(0).add(0,-1,0), OUTLINE.get(1).add(0, -20, 0));

		// clear items
		List<Entity> Entities = blockLocation.getWorld().getEntities();
		
		for (Entity current : Entities) {
			if (current.getType() != EntityType.DROPPED_ITEM) {
				continue;
			}
			
			if(current.getLocation().distance(blockLocation) < 30) {
				current.remove();
			}
		}

		Plugin plugin = Tycoon.getPlugin(); // Your plugin's instance
		HolographicDisplaysAPI api = HolographicDisplaysAPI.get(plugin); // The API instance for your plugin

		// clear holograms
		for(Hologram holo : api.getHolograms()) {
			if(holo.getPosition().getWorldName().equals(blockLocation.getWorld().getName())) {
				if(holo.getPosition().toLocation().distance(blockLocation) < 20) {
					holo.delete();
				}
			}
		}
	}

	public static Location getTycoonLocation(String UUID) {
		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");
		ConfigurationSection t = tycoons.getConfigurationSection("tycoons");
		
		double x = t.getDouble(UUID + ".location.x");
		double y = t.getDouble(UUID + ".location.y");
		double z = t.getDouble(UUID + ".location.z");
		
		World world = Bukkit.getWorld(t.getString(UUID + ".location.world"));

		return new Location(world, x,y,z);
	}
	
	public static ConfigurationSection getTycoon(String UUID) {
		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");
		ConfigurationSection t = tycoons.getConfigurationSection("tycoons");
		return t.getConfigurationSection(UUID);
	}
	
	
	public static Double getTycoonDirection(String UUID) {		
		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");
		ConfigurationSection t = tycoons.getConfigurationSection("tycoons");

		return t.getDouble(UUID + ".location.direction");
	}

	
	public static Boolean canPlaceTycoon(Location l) {
		boolean canPlace = true;
		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");
		ConfigurationSection t = tycoons.getConfigurationSection("tycoons");
		
		for(String loc : t.getKeys(false)) {
			if(PlayerUtils.validUUID(loc)) {
				if(l.distance(getTycoonLocation(loc)) < Tycoon.SEL_RADIUS) {
					canPlace = false;
					break;
				}
			}
		}
		
		return canPlace;
	}
}
