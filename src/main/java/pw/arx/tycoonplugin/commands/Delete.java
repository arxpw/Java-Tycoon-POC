package pw.arx.tycoonplugin.commands;

import org.bukkit.entity.Player;
import pw.arx.tycoonplugin.managers.TycoonManager;
import pw.arx.tycoonplugin.utils.StringUtils;

public class Delete {
	public static Boolean Command(Player p) {
    	if(!TycoonManager.hasTycoon(p)) {
    		p.sendMessage(StringUtils.c("&cYou need to own a tycoon to delete one! /tycoon create"));
    		return false;
    	} else {
    		TycoonManager.deleteTycoon(p);
    		p.sendMessage(StringUtils.c("&aTycoon Deleted."));
    		return true;
    	}
	}
}
