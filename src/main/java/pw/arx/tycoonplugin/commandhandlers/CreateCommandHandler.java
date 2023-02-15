package pw.arx.tycoonplugin.commandhandlers;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pw.arx.tycoonplugin.Tycoon;
import pw.arx.tycoonplugin.managers.TycoonManager;
import pw.arx.tycoonplugin.utils.StringUtils;

import java.io.FileNotFoundException;

public class CreateCommandHandler implements CommandHandler {
	public static Boolean handle(Player p) {
    	if (!Tycoon.SELECTION_LOCATIONS.containsKey(p.getUniqueId())) {
    		p.sendMessage(StringUtils.c("&cYou have to make a selection first! &f/tycoon tool"));
    		return false;
    	} else {
    		Location selectedLoc = Tycoon.SELECTION_LOCATIONS.get(p.getUniqueId());
    		
    		if(TycoonManager.canPlaceTycoon(selectedLoc)) {
				try {
					TycoonManager.spawnTycoon(selectedLoc, p);
				} catch (FileNotFoundException ex) {}
                return true;
    		} else {
    			p.sendMessage(StringUtils.c("&cToo close to another tycoon!"));
    			return false;
    		}

    	}
	}
}
