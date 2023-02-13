package pw.arx.tycoonplugin.utils;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class PlayerUtils {
	private PlayerUtils() { }
	
	public static Boolean validUUID(String UUID) {
		
		if(UUID.length() < 20 && UUID.length() > 50)
			return false;

		if (UUID.matches("[a-f0-9]{8}-[a-f0-9]{4}-4[0-9]{3}-[89ab][a-f0-9]{3}-[0-9a-f]{12}"))
	        return true;
		
		return true;
	}
	
	public static void denySound(Player p) {
		p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
	}
	
}
