package pw.arx.tycoonplugin.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.arx.tycoonplugin.commandhandlers.*;
import pw.arx.tycoonplugin.utils.StringUtils;

public class CommandListener implements CommandExecutor {

	private CommandSender sender;
	private String command;
	private Player player;

	private boolean isCommand(String commandToCheck)
	{
		return command.equalsIgnoreCase(commandToCheck);
	}

	private void displayCommandHelp()
	{
		sender.sendMessage(StringUtils.lng("COMMANDS_TITLE"));
		sender.sendMessage(StringUtils.lng("COMMANDS"));
	}

	private void displayNoConsoleError()
	{
		sender.sendMessage(StringUtils.lng("NO_CONSOLE"));
	}

	private void displayInvalidCommandError()
	{
		sender.sendMessage(StringUtils.lng("INVALID_COMMAND"));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		this.sender = sender;

		if (args.length == 0) {
			displayCommandHelp();
			return false;
		}
		
		if (!(sender instanceof Player)) {
			displayNoConsoleError();
			return false;
		}
		
		// we now know this is a player
		Player p = (Player) sender;

		this.player = p;
		this.command = args[0];

		if (isCommand("tool")) {
			return ToolBuildCommandHandler.Handle(p);
		}

		if (isCommand("build")) {
			return ToolCreateCommandHandler.Handle(p);
		}
		
		if (isCommand("delete")) {
			return DeleteCommandHandler.Handle(p);
		}
		
		if (isCommand("reload")) {
			return ReloadCommandHandler.Handle(p);
		}

		if (isCommand("machines")) {
			return MachineCommandHandler.Handle(p);
		}
		
		if (isCommand("admin")) {
			return AdminCommandHandler.Handle(p);
		}

		displayInvalidCommandError();
		return false;
	}
}
