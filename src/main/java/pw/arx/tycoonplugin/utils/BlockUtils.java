package pw.arx.tycoonplugin.utils;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pw.arx.tycoonplugin.Tycoon;
import pw.arx.tycoonplugin.managers.FileManager.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public final class BlockUtils {
	
	// prevent this class being constructed
	private BlockUtils() { } 
	
	public static Block targetBlock(Player p) {
	   Block b = p.getTargetBlock(null, 200);
	   return b;
	}

	private static void buyBox(Location l, String owner, String hololine1, String hololine2, String hololine3) {
		boolean signLogic = false;
		String facing = "NORTH";
		Block bl = l.getBlock();

		if(bl.getType() == Material.OAK_SIGN) {
			signLogic = true;
			org.bukkit.material.Sign sign = (org.bukkit.material.Sign) bl.getState().getData();
			facing = LocUtils.invertFacingDirection(sign.getFacing().name());
		}
		
		bl.setType(Material.PLAYER_HEAD);
		Skull sk = (Skull) bl.getState();
		
		if (signLogic) {
			sk.setRotation(BlockFace.valueOf(facing));
		}
		
		sk.setOwner(owner);
		sk.update();

		List<String> currentLines = Arrays.asList(hololine1, hololine2, hololine3);
		Location loc = l.getBlock().getLocation().add(Tycoon.HOLOGRAM_OFFSET);

		spawnHologramFromLines(currentLines, loc);
		
//		SkullCreator.blockWithBase64(bl, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L" + "3RleHR1cmUvNTIyODRlMTMyYmZkNjU5YmM2YWRhNDk3YzRmYTMwOTRjZDkzMjMxYTZiNTA1YTEyY2U3Y2Q1MTM1YmE4ZmY5MyJ9fX0=");
	}

	private static final Logger log = Logger.getLogger("Minecraft");

	private static void spawnHologramFromLines(List<String> lines, Location location)
	{
		HolographicDisplaysAPI holoApi = Tycoon.getPlugin().getHologramsApi();
		Hologram hologram = null;

		// stop the duplication of existing holograms.
		for (Hologram h : holoApi.getHolograms()) {
			double dist = h.getPosition().toLocation().distance(location);
			if (dist < 2.5) {
				hologram = h;
				h.getLines().clear();
				break;
			}
		}

		// if the hologram does not already exist, we can create a new one.
		if (hologram == null) {
			hologram = holoApi.createHologram(location);
		}

		for(String line : lines) {
			hologram.getLines().appendText(StringUtils.c(line));
		}
	}
	
	public static void loadHolograms() {
		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");

		for(String tycoonUUID : tycoons.getConfigurationSection("tycoons").getKeys(false)) {
			// tycoon exists..
			if(tycoons.getConfigurationSection("tycoons." + tycoonUUID + ".clickers") != null) {
				// clickers exists..
				for(String clicker : tycoons.getConfigurationSection("tycoons." + tycoonUUID + ".clickers").getKeys(false)) {
					String clickers_string = "tycoons." + tycoonUUID + ".clickers." + clicker;

					ConfigurationSection THISCLICKER = tycoons.getConfigurationSection(clickers_string);
					ConfigurationSection THISTYCOON = tycoons.getConfigurationSection("tycoons." + tycoonUUID);

					Location l = new Location(
						Bukkit.getWorld(THISTYCOON.getString(".location.world")),
						THISCLICKER.getDouble(".location.x"),
						THISCLICKER.getDouble(".location.y"),
						THISCLICKER.getDouble(".location.z")
					);

					List<String> currentLines = THISCLICKER.getStringList(".lines");

					Location loc = l.getBlock().getLocation().add(Tycoon.HOLOGRAM_OFFSET);
					spawnHologramFromLines(currentLines, loc);
				}
			}
		}
	}
	
	public static void signBuyers(Player p, Location l1, Location l2) {
		Cuboid cuboid = new Cuboid(l1, l2);
		
        for (Block block : cuboid) {
        	// we use OAK SIGNS to template items within a schematic
        	if (!block.getType().equals(Material.OAK_SIGN)) {
				continue;
			}
        	
        	Sign s = (Sign)block.getState();
        	String first_line = s.getLine(0);
        	String second_line = s.getLine(1);
        	String third_line = s.getLine(2);
        	String fourth_line = s.getLine(3);
        	
        	String skull_owner = "MHF_Chest";
        	
        	if (fourth_line.length() > 2) {
				skull_owner = fourth_line;
			}
        	
        	if (first_line.equals("buy")) {
        		String holo_line_1 = "&6&lBUILDING";
            	String holo_line_2 = "BUY &6" + second_line.toUpperCase();
            	String holo_line_3 = "&a$" + third_line;
            	
            	// save these locations for holograms..
            	String UUID = p.getUniqueId().toString();
        		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");
        		
        		String CLICKER_PREFIX = "tycoons." + UUID + ".clickers." + second_line;
        		String CLICKER_LOC_PREFIX = CLICKER_PREFIX + ".location";
        		Location BLOC = block.getLocation();
        		
        		List<String> LINES = new ArrayList<>(); 
        		LINES.add(holo_line_1);
        		LINES.add(holo_line_2);
        		LINES.add(holo_line_3);
        		
        		tycoons.set(CLICKER_PREFIX + ".lines", LINES);
        		tycoons.set(CLICKER_LOC_PREFIX + ".x", BLOC.getX());
        		tycoons.set(CLICKER_LOC_PREFIX + ".y", BLOC.getY());
        		tycoons.set(CLICKER_LOC_PREFIX + ".z", BLOC.getZ());

        		tycoons.save();
        		
        		buyBox(block.getLocation(), skull_owner, holo_line_1, holo_line_2, holo_line_3);
				continue;
        	}
        	
        	if(first_line.equals("owner")) {
            	String holo_line_1 = "&6&lOWNER";
            	String holo_line_2 = "&a" + p.getName();
            	
            	// save these locations for holograms..
            	String UUID = p.getUniqueId().toString();
        		Config tycoons = Tycoon.fileManager.getConfig("tycoons.yml");
        		
        		String CLICKER_PREFIX = "tycoons." + UUID + ".clickers." + first_line;
        		String CLICKER_LOC_PREFIX = CLICKER_PREFIX + ".location";
        		Location BLOC = block.getLocation();
        		
        		List<String> LINES = new ArrayList<>(); 
        		LINES.add(holo_line_1);
        		LINES.add(holo_line_2);
        		
        		tycoons.set(CLICKER_PREFIX + ".lines", LINES);
        		tycoons.set(CLICKER_LOC_PREFIX + ".x", BLOC.getX());
        		tycoons.set(CLICKER_LOC_PREFIX + ".y", BLOC.getY());
        		tycoons.set(CLICKER_LOC_PREFIX + ".z", BLOC.getZ());

        		tycoons.save();
        		
        		buyBox(block.getLocation(), skull_owner, holo_line_1, holo_line_2, third_line);
        	}
        }
	}
	
	public static Boolean insideCuboid(Location l, Location l1, Location l2) {
		Cuboid cuboid = new Cuboid(l1, l2);
		boolean found = false;
    	for (Block block : cuboid) {
    		if(l.getBlock().equals(block)) {
    			found = true;
    			break;
    		}
    	}
    	return found;
	}
	
	public static void blockUpdate(Location l1, Location l2, Player p) {
		Cuboid cuboid = new Cuboid(l1, l2);
    	for (Block block : cuboid) {
    		p.sendBlockChange(block.getLocation(), block.getBlockData());
    		p.sendBlockChange(block.getLocation().add(0,-1,0), block.getBlockData());
    	}
	}
	
	public static void blockFillFakes(Player p, Location l1, Location l2, Location center) {
		Cuboid cuboid = new Cuboid(l1, l2);
		int borderWidth = 2;
		p.sendBlockChange(center, Material.GOLD_BLOCK.createBlockData());
		// looping through every block..
		
		int blockCount = 0;
        for (Block block : cuboid) {
        	Location BL = block.getLocation();
        	blockCount++;

        	if(!(BL.getX() < l1.getX()-borderWidth && BL.getZ() < l1.getZ()-borderWidth && BL.getX() > l2.getX()+borderWidth && BL.getZ() > l2.getZ()+borderWidth)) {
        		
        		BlockData bd = Material.RED_NETHER_BRICKS.createBlockData();
        		BlockData bd_outline = Material.GRAY_STAINED_GLASS.createBlockData();
        		
        		if(blockCount % 6 == 0) {
        			bd = Material.MAGMA_BLOCK.createBlockData();
        			bd_outline = Material.GREEN_STAINED_GLASS.createBlockData();
        		}
        		
        		if(blockCount % 21 == 0 || blockCount % 5 == 0) {
        			if(blockCount % 3 == 0)
        				bd_outline = Material.LIME_STAINED_GLASS.createBlockData();
        			bd = Material.NETHERRACK.createBlockData();
        			
        		}
        		
        		if(blockCount % 13 == 0 || blockCount % 3 == 0)
        			bd = Material.NETHERRACK.createBlockData();
        		
        		if(blockCount % 42 == 0 || blockCount % 19 == 0)
        			if(blockCount %6 == 0)
        				bd_outline = Material.WHITE_STAINED_GLASS.createBlockData();
        		
        		p.sendBlockChange(block.getLocation(), bd_outline);
        		p.sendBlockChange(block.getLocation().add(0,-1,0), bd);
        	}
        }
	}
	
	public static void blockOutline(Material m, Location l1, Location l2) {
		Cuboid cuboid = new Cuboid(l1, l2);
        for (Block block : cuboid) {
        	Location BL = block.getLocation();
        	if (!(BL.getX() < l1.getX() && BL.getZ() < l1.getZ() && BL.getX() > l2.getX() && BL.getZ() > l2.getZ())) {
        		block.setType(m);
        	}
        }
	}
		
	public static void blockFill(Material m, Location l1, Location l2) {
		Cuboid cuboid = new Cuboid(l1, l2);
        for (Block block : cuboid)
        	block.setType(m);
	}
}
