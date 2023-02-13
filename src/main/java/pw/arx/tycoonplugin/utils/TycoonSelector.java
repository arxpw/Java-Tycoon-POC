package pw.arx.tycoonplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pw.arx.tycoonplugin.Tycoon;

import java.util.ArrayList;

public class TycoonSelector {
	private TycoonSelector() { }
	
	public static ArrayList<Location> selectOffset(Location l, Integer BOX_OFFSET) {
		ArrayList<Location> RETURNLOC = new ArrayList<Location>();
			
		RETURNLOC.add(new Location(l.getWorld(), l.getX() + BOX_OFFSET, l.getY(), l.getZ() + BOX_OFFSET));
		RETURNLOC.add(new Location(l.getWorld(), l.getX() - BOX_OFFSET, l.getY(), l.getZ() - BOX_OFFSET));
		
		return RETURNLOC;
	}
	
	public static boolean ClearSelection(Player p) {
		if(Tycoon.TOOLS_ENABLED.containsKey(p.getUniqueId())) {
        	ArrayList<Location> locarray = LocUtils.selectOffset(Tycoon.SELECTION_LOCATIONS.get(p.getUniqueId()), Tycoon.SEL_OFFSET);
            BlockUtils.blockUpdate(locarray.get(0), locarray.get(1), p);
			Tycoon.SELECTION_LOCATIONS.remove(p.getUniqueId());
			return true;
		} else {
			return false;
		}
	}
	
	public static void SelectBlocks(Player p) {
		Block tblock = BlockUtils.targetBlock(p);
		
		if(!Tycoon.TOOLS_ENABLED.containsKey(p.getUniqueId()))
			Tycoon.TOOLS_ENABLED.put(p.getUniqueId(), true);
			
        Tycoon.SELECTION_LOCATIONS.put(p.getUniqueId(), tblock.getLocation());
        
		// + FAKE UPDATE BLOCKS...
        ArrayList<Location> NEW_LOC = LocUtils.selectOffset(tblock.getLocation(), Tycoon.SEL_OFFSET);
        BlockUtils.blockFillFakes(p, NEW_LOC.get(0), NEW_LOC.get(1), tblock.getLocation());
        
        
    	Bukkit.getScheduler().scheduleSyncDelayedTask(Tycoon.getPlugin(), new Runnable() {
            public void run() {
            	p.sendBlockChange(tblock.getLocation(), Material.GOLD_BLOCK.createBlockData());
            }
        }, 3);
        
        
        p.sendMessage(StringUtils.lng("LAND_VALID_SELECT"));
	}
	
}
