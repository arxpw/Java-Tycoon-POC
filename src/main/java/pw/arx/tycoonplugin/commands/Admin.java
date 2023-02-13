package pw.arx.tycoonplugin.commands;

import org.bukkit.entity.Player;
import pw.arx.tycoonplugin.Tycoon;
import pw.arx.tycoonplugin.utils.StringUtils;

public class Admin {
	public static Boolean Command(Player p) {
		if(!p.hasPermission(Tycoon.MAIN_COMMAND + ".admin" )) {
			p.sendMessage(StringUtils.lng("NO_PERMISSION"));
			return false;
		}
		
    	if(!Tycoon.ADMINS.containsKey(p.getUniqueId())) {
    		Tycoon.ADMINS.put(p.getUniqueId(), true);
    		p.sendMessage(StringUtils.c("Admin Mode &aEnabled."));
    		return true;
    	} else {
    		Tycoon.ADMINS.remove(p.getUniqueId());
    		p.sendMessage(StringUtils.c("Admin Mode &cDisabled."));
    		return true;
    	}
	}
}
