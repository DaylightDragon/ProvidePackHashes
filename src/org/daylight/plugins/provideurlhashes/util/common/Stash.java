package org.daylight.plugins.provideurlhashes.util.common;

import static org.daylight.plugins.provideurlhashes.util.common.SimpleLogger.debug;
import static org.daylight.plugins.provideurlhashes.util.common.SimpleLogger.log;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Stash {
	public static boolean isSenderAPlayer(CommandSender sender) {
		if(sender instanceof ConsoleCommandSender) {
			sender.sendMessage(ChatColor.RED + "You can't do it via console!");
			return false;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You can use it only as a player!");
			return false;
		}
		return true;
	}
	
	public static boolean isSenderAPlayerOrConsole(CommandSender sender) {
		if(!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + "You can use this only as a player or console!");
			return false;
		}
		return true;
	}

	public static boolean isSenderConsole(CommandSender sender) {
		if(!(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + "You can use this only from console!");
			return false;
		}
		return true;
	}
}