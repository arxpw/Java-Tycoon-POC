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
	
	public static boolean ClearSelection(Player p)
	{
		if (!Tycoon.TOOLS_ENABLED.containsKey(p.getUniqueId())) {
			return false;
		}

		ArrayList<Location> selectionLocations = LocUtils.selectOffset(Tycoon.SELECTION_LOCATIONS.get(p.getUniqueId()), Tycoon.SEL_OFFSET);
		BlockUtils.blockUpdate(selectionLocations.get(0), selectionLocations.get(1), p);
		Tycoon.SELECTION_LOCATIONS.remove(p.getUniqueId());

		return true;
	}
	
	public static void SelectBlocks(Player p)
	{
		Block targetBlock = BlockUtils.targetBlock(p);
		
		if (!Tycoon.TOOLS_ENABLED.containsKey(p.getUniqueId())) {
			Tycoon.TOOLS_ENABLED.put(p.getUniqueId(), true);
		}
			
        Tycoon.SELECTION_LOCATIONS.put(p.getUniqueId(), targetBlock.getLocation());
        
		// + FAKE UPDATE BLOCKS...
        ArrayList<Location> newLocations = LocUtils.selectOffset(targetBlock.getLocation(), Tycoon.SEL_OFFSET);
        BlockUtils.blockFillFakes(p, newLocations.get(0), newLocations.get(1), targetBlock.getLocation());

    	Bukkit.getScheduler().scheduleSyncDelayedTask(Tycoon.getPlugin(), new Runnable() {
            public void run() {
            	p.sendBlockChange(targetBlock.getLocation(), Material.GOLD_BLOCK.createBlockData());
            }
        }, 3);

        p.sendMessage(StringUtils.lng("LAND_VALID_SELECT"));
	}
}
