package pw.arx.tycoonplugin.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.arx.tycoonplugin.commands.Admin;
import pw.arx.tycoonplugin.commands.Delete;
import pw.arx.tycoonplugin.commands.Reload;
import pw.arx.tycoonplugin.commands.Tool;
import pw.arx.tycoonplugin.utils.PlayerUtils;
import pw.arx.tycoonplugin.utils.StringUtils;

public class CommandListener implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {

		if (args.length == 0) {
			sender.sendMessage(StringUtils.lng("COMMANDS_TITLE"));
			sender.sendMessage(StringUtils.lng("COMMANDS"));
			return false;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(StringUtils.lng("NO_CONSOLE"));
			return false;
		}
		
		// we know this is a player..
		Player p = (Player) sender;
		
		if(args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("tool")) {
			return Tool.Creator(p);
		}
		
		if(args[0].equalsIgnoreCase("delete")) {
			return Delete.Command(p);
		}
		
		if (args[0].equalsIgnoreCase("reload")) {
			return Reload.Command(p);
		}
		
		if(args[0].equalsIgnoreCase("build")) {
			return Tool.Builder(p);
		}
		
		if(args[0].equalsIgnoreCase("admin")) {
			return Admin.Command(p);
		}
		
		p.sendMessage(StringUtils.lng("INVALID_COMMAND"));
		return true;
		
	}
}
