package pw.arx.tycoonplugin.managers;

import org.bukkit.entity.Player;
import pw.arx.tycoonplugin.Tycoon;

public class AdminManager {
	public static Boolean isAdmin(Player p) {
		if(Tycoon.ADMINS.containsKey(p.getUniqueId()))
			return true;
		return false;
	}
}
