package com.saucesum.mc.koth;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 
 * King of The Hill command executor
 * 
 * @author Ravn T-M
 *
 */
public class KoTHCommandExecutor implements CommandExecutor {

	private KoTH koth;

	public KoTHCommandExecutor(KoTH koth) {
		this.koth = koth;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		if (args == null || args.length == 0 || args[0].isEmpty()) {
			sendInvalidUsageMessage(sender);
			return true;
		}

		Player player = (Player) sender;

		switch (args[0].toLowerCase().charAt(0)) {
		case 'a': // Add
			koth.commandAdd(player);
			break;

		case 'r': // Remove
		case 'd': // Delete
			koth.commandRemove(player);
			break;

		case 'c': // Clear
			koth.commandClear(player);
			break;

		case 'i': // Initialize
			koth.commandInitialize(player);
			break;

		case 'g': // Generate (for testing)
			koth.commandGenerate(player);
			break;

		default:
			sendInvalidUsageMessage(sender);
		}

		return true;
	}

	private void sendInvalidUsageMessage(CommandSender sender) {
		sender.sendMessage("Invalid usage. Commands " + ChatColor.RED + "add remove clear init gen");
	}

}
