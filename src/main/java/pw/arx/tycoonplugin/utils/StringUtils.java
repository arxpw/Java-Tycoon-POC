package pw.arx.tycoonplugin.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import pw.arx.tycoonplugin.Tycoon;

public final class StringUtils {
	// prevent this class being constructed
	private StringUtils() { }
	
	public static String c(String i) {
		return ChatColor.translateAlternateColorCodes('&',i);
	}
	
	public static String lng(String i) {
		return c(Tycoon.fileManager.getConfig("lang.yml").get(i).toString()
			.replace("., ", ".\n&r")
			.replace("[/", "&r/")
			.replace(".]", ".")
				);
	}
	
	public static String cap(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	public static String changed(String before, String after) {
		if(before.equals(after)) {
			return "";
		}
		return c(" &4&l<-&r&4 Changed ");
	}
	
	public static String configBit(String find, FileConfiguration before, FileConfiguration after) {
		return after.getString(find) + changed(before.getString(find), after.getString(find));
	}
	
	public static String loQuick(Location l) {
		return "x:" + l.getX() + "," + "y:" + l.getY() + "," + "z:" + l.getZ();
	}
	
}
