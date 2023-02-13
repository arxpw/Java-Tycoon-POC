package pw.arx.tycoonplugin.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pw.arx.tycoonplugin.Tycoon;
import pw.arx.tycoonplugin.utils.BlockUtils;
import pw.arx.tycoonplugin.utils.ItemUtils;
import pw.arx.tycoonplugin.utils.LocUtils;
import pw.arx.tycoonplugin.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Tool {
	public static boolean Creator(Player p) {
		HashMap<UUID, Boolean> h = Tycoon.TOOLS_ENABLED;
		boolean FINAL_RETURN = false;
		
		if (h.containsKey(p.getUniqueId())) {
			if (h.get(p.getUniqueId()) == true) {
				h.put(p.getUniqueId(), false);
				ItemUtils.ToggleSelector(p, false, ItemUtils.LandSelector(true), ItemUtils.LandSelector(false));
				HashMap<UUID, Location> SEL_HASHMAP = Tycoon.SELECTION_LOCATIONS;
				if(SEL_HASHMAP.containsKey(p.getUniqueId())) {
					ArrayList<Location> LOC_ARRAY = LocUtils.selectOffset(SEL_HASHMAP.get(p.getUniqueId()), Tycoon.SEL_OFFSET);
                	BlockUtils.blockUpdate(LOC_ARRAY.get(0), LOC_ARRAY.get(1), p);
                	p.sendMessage(StringUtils.c("Land selection &bcleared&r!"));
                	SEL_HASHMAP.remove(p.getUniqueId());
                	FINAL_RETURN = false;
				}
			} else {
				h.put(p.getUniqueId(), true);
				ItemUtils.ToggleSelector(p, true, ItemUtils.LandSelector(true), ItemUtils.LandSelector(false));
				FINAL_RETURN = true;
			}
		} else {
			h.put(p.getUniqueId(), true);
			ItemUtils.ToggleSelector(p, true, ItemUtils.LandSelector(true), ItemUtils.LandSelector(false));
		}
		p.sendMessage(StringUtils.c("Tool &a" + (h.get(p.getUniqueId()) == true ? "&aEnabled" : "&cDisabled")));
		return FINAL_RETURN;
	}
	
	public static boolean Builder(Player p) {
		HashMap<UUID, Boolean> h = Tycoon.BUILDER_TOOL_ENABLED;
		boolean FINAL_RETURN = false;
		
		if (h.containsKey(p.getUniqueId())) {
			if (h.get(p.getUniqueId()) == true) {
				h.put(p.getUniqueId(), false);
				ItemUtils.ToggleSelector(p, false, ItemUtils.LandSelector(true), ItemUtils.LandSelector(false));
				HashMap<UUID, Location> SEL_HASHMAP = Tycoon.BUILDER_TOOL_LOCATIONS;
				if (SEL_HASHMAP.containsKey(p.getUniqueId())) {
					ArrayList<Location> LOC_ARRAY = LocUtils.selectOffset(SEL_HASHMAP.get(p.getUniqueId()), Tycoon.SEL_OFFSET);
                	BlockUtils.blockUpdate(LOC_ARRAY.get(0), LOC_ARRAY.get(1), p);
                	p.sendMessage(StringUtils.c("Builder selection &bcleared&r!"));
                	SEL_HASHMAP.remove(p.getUniqueId());
                	FINAL_RETURN = false;
				}
			} else {
				h.put(p.getUniqueId(), true);
				ItemUtils.ToggleSelector(p, true, ItemUtils.LandSelector(true), ItemUtils.LandSelector(false));
				FINAL_RETURN = true;
			}
		} else {
			h.put(p.getUniqueId(), true);
			ItemUtils.ToggleSelector(p, true, ItemUtils.LandSelector(true), ItemUtils.LandSelector(false));
		}
		p.sendMessage(StringUtils.c("Tool &a" + (h.get(p.getUniqueId()) == true ? "&aEnabled" : "&cDisabled")));
		return FINAL_RETURN;
	}
}
