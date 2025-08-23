package org.daylight.plugins.provideurlhashes.util.common;

import static org.daylight.plugins.provideurlhashes.util.common.SimpleLogger.debug;
import static org.daylight.plugins.provideurlhashes.util.common.SimpleLogger.log;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.daylight.plugins.provideurlhashes.main.Main;

public class Stash {
	public static boolean isSenderAPlayer(CommandSender sender) {
		if(sender instanceof ConsoleCommandSender) {
			sender.sendMessage(ChatColor.RED + "You can't do this via console!");
			return false;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You can use this only as a player!");
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

	public static void logAndSend(CommandSender sender, String message) {
//		Main.plugin.getLogger().info(message); // no colors! :(

		String messageWBrackets = "[" +SimpleLogger.pluginName + "] " + message;
		if(sender != null) {
			if(sender instanceof ConsoleCommandSender) sender.sendMessage(messageWBrackets);
			else sender.sendMessage(message);
		}
		if(!(sender instanceof ConsoleCommandSender)) Bukkit.getConsoleSender().sendMessage(messageWBrackets); // Sending to console
	}

	public static void logSevereAndSend(CommandSender sender, String message) {
//		Main.plugin.getLogger().severe(message);

		String messageWBrackets = "[" +SimpleLogger.pluginName + "] " + ChatColor.RED + message;
		if(sender != null) {
			if(sender instanceof ConsoleCommandSender) sender.sendMessage(messageWBrackets);
			else sender.sendMessage(ChatColor.RED + message);
		}
		if(!(sender instanceof ConsoleCommandSender)) Bukkit.getConsoleSender().sendMessage(messageWBrackets); // Sending to console
	}
}