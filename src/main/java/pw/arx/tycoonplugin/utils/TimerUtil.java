package pw.arx.tycoonplugin.utils;

import org.bukkit.scheduler.BukkitRunnable;
import pw.arx.tycoonplugin.Tycoon;
import pw.arx.tycoonplugin.managers.TycoonManager;

public class TimerUtil {
	
	public static void DoTimer() {
		new BukkitRunnable() {
		    public void run() {
		        TycoonManager.addTycoonGains();
		    }
		}.runTaskTimer(Tycoon.getPlugin(), 0L, 20L*60);
	}
}
