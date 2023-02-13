package pw.arx.tycoonplugin.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pw.arx.tycoonplugin.Tycoon;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class WorldEditUtils {

	public static Boolean playerInsideTheirRegion(Player p) {
		
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(p.getWorld()));
		Boolean returning = false;
		
		for(ProtectedRegion r : regions.getApplicableRegions(BukkitAdapter.asBlockVector(p.getLocation()))) {
			if(r.getOwners().contains(p.getUniqueId())) {
				returning = true;
				break;
			}
         }
		
		return returning;
	}
	
	public static Boolean locationInsideTycoon(Location l) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(l.getWorld()));
		Boolean returning = false;
		
		for(ProtectedRegion r : regions.getApplicableRegions(BukkitAdapter.asBlockVector(l))) {
			if(PlayerUtils.validUUID(r.getId())) {
				returning = true;
				break;
			}
         }
		
		return returning;
	}
	
	public static void regionCreate(Location l, Player p) {
		p.sendMessage("Attempting to create a WG region for you..");
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		
		ArrayList<Location> REGION_ARRAY = LocUtils.selectOffset(l, Tycoon.SEL_OFFSET);
		Location l1 = REGION_ARRAY.get(0);
		Location l2 = REGION_ARRAY.get(1);
		
		BlockVector3 min = BlockVector3.at(l1.getX(), 0, l1.getZ());
		BlockVector3 max = BlockVector3.at(l2.getX(), 255, l2.getZ());
		ProtectedRegion region = new ProtectedCuboidRegion(p.getUniqueId().toString(), min, max);
		
		region.setFlag(Flags.GREET_MESSAGE, StringUtils.c("Welcome to " + p.getDisplayName() + "'s Tycoon."));
		
		DefaultDomain owner = region.getOwners();
		owner.addPlayer(p.getUniqueId());

		RegionManager regions = container.get(BukkitAdapter.adapt(p.getWorld()));
		regions.addRegion(region);
	}
	
	public static void regionDelete(Player p) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(p.getWorld()));
		regions.removeRegion(p.getUniqueId().toString());
		
	}
	
    public static void loadSchematic(Location l, Double rotation, String schematic) {

    	World world = BukkitAdapter.adapt(l.getWorld());
    	File file = new File(Tycoon.getPlugin().getDataFolder() + File.separator + "schematics" + File.separator + schematic + ".schem");
		log.severe(Tycoon.getPlugin().getDataFolder() + File.separator + "schematics" + File.separator + schematic + ".schem");

		Clipboard clipboard;
		ClipboardFormat format = ClipboardFormats.findByFile(file);

    	try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
    	   clipboard = reader.read();
    	   
    	   try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) { 
    		   
    		   ClipboardHolder CHOLDER = new ClipboardHolder(clipboard);
    		   AffineTransform transform = new AffineTransform();
    		   
    		   transform = transform.rotateY(rotation);
    		   
    		   CHOLDER.setTransform(CHOLDER.getTransform().combine(transform));
    		   
    		   Operation operation = CHOLDER
		            .createPaste(editSession)
		            .to(BlockVector3.at(l.getX(), l.getY(), l.getZ()))
		            .ignoreAirBlocks(false)
		            .build();
    		   try {
    			   Operations.complete(operation);
    		   } catch (WorldEditException e) {
    			   // TODO Auto-generated catch block
    			   e.printStackTrace();
    		   }
    		}
    	   
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	private static final Logger log = Logger.getLogger("Minecraft");
	
    public static void loadTycoonSchematic(Location l, Player p, String schematic) throws FileNotFoundException {
    	World world = BukkitAdapter.adapt(l.getWorld());
		String schematicFile = Tycoon.getPlugin().getDataFolder() + File.separator + "schematics" + File.separator + schematic + ".schem";
    	File file = new File(schematicFile);

		if (!file.exists()) {
			p.sendMessage("The schematic " + schematic + ".schem &cwas not found in your files. &rStopping.");
			return;
		}

		log.info("File exists: " + file.exists());

		Clipboard clipboard;
		ClipboardFormat format = ClipboardFormats.findByFile(file);

		if (format == null || !format.isFormat(file)) {
			p.sendMessage("The schematic " + schematic + ".schem does &cnot seem to be a valid format. &rStopping.");
			return;
		}

		InputStream fileStream = new FileInputStream(file);

		try (ClipboardReader reader = format.getReader(fileStream)) {
    	   clipboard = reader.read();
    	   
    	   try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) { 
    		   
    		   ClipboardHolder CHOLDER = new ClipboardHolder(clipboard);
    		   AffineTransform transform = new AffineTransform();
    		   transform = transform.rotateY(LocUtils.schematicRotateAmount(p));
    		   CHOLDER.setTransform(CHOLDER.getTransform().combine(transform));
    		   
    		   Operation operation = CHOLDER
		            .createPaste(editSession)
		            .to(BlockVector3.at(l.getX(), l.getY(), l.getZ()))
		            .ignoreAirBlocks(false)
		            .build();
    		   try {
    			   Operations.complete(operation);
    		   } catch (WorldEditException e) {
    			   // TODO Auto-generated catch block
    			   e.printStackTrace();
    		   }
    		}
    	   
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
