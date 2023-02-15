package pw.arx.tycoonplugin.commandhandlers;

import org.bukkit.entity.Player;
import pw.arx.tycoonplugin.Tycoon;
import pw.arx.tycoonplugin.utils.StringUtils;

public class ReloadCommandHandler implements CommandHandler {
	public static boolean handle(Player p) {
		if(!p.hasPermission(Tycoon.MAIN_COMMAND + ".reload")) {
			p.sendMessage(StringUtils.lng("NO_PERMISSION"));
			return false;
		} else {
			Tycoon.fileManager.loadConfigs();
			p.sendMessage(StringUtils.lng("RELOADED"));
			return true;
		}
	}
}
