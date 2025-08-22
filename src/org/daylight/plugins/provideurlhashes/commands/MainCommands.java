package org.daylight.plugins.provideurlhashes.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.daylight.plugins.provideurlhashes.config.ConfigData;
import org.daylight.plugins.provideurlhashes.config.ConfigOperations;
import org.daylight.plugins.provideurlhashes.config.UserData;
import org.daylight.plugins.provideurlhashes.util.PackUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class MainCommands {
    public static boolean toggleSaving(CommandSender sender, String[] args) {
        ConfigData.saveData = !ConfigData.saveData;
        ConfigOperations.saveConfig();
        String line = (ConfigData.saveData ? "" + ChatColor.GREEN + ChatColor.BOLD + "Enabled" : "" + ChatColor.RED + ChatColor.BOLD + "Disabled");
        line += "" + ChatColor.RESET + ChatColor.GRAY + " saving hash data";

        sender.sendMessage(line);
        return true;
    }

    public static boolean updateHash(CommandSender sender, String[] args) {
        PackUtils.downloadPackIfNeeded(true, null);
        sender.sendMessage(ChatColor.GOLD + "Started downloading the pack to update the hash, check the console for more details");
        return true;
    }

    public static boolean setNewPackUrl(CommandSender sender, String[] args) {
        String url = String.join("", args).replace(" ", "%20").trim().replace("\\", "");
        PackUtils.downloadPackIfNeeded(true, url);

        Path serverPropertiesPath = Paths.get("server.properties");
        if(!Files.exists(serverPropertiesPath)) {
            sender.sendMessage(ChatColor.RED + "Couldn't find the server.properties file to save it forever");
            return true;
        }

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(serverPropertiesPath)) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        props.setProperty("resource-pack", url);

        try (OutputStream out = Files.newOutputStream(serverPropertiesPath)) {
            props.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        sender.sendMessage(ChatColor.GOLD + "Started downloading the pack to update the hash, check console for more details");
        sender.sendMessage(ChatColor.GOLD + "Please note that the players will need to re-join the server to see changes!");
        return true;
    }

    public static boolean printState(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "State:");
        sender.sendMessage("Last known Pack's ID: " + ChatColor.GREEN + toStringOrNone(UserData.lastId));
        sender.sendMessage("Last known Pack's URI: " + ChatColor.GREEN + toStringOrNone(UserData.lastUri));
        sender.sendMessage("Last known Pack's Hash: " + ChatColor.GREEN + toStringOrNone(UserData.lastHash));
        return true;
    }

    private static String toStringOrNone(Object o) {
        if(o != null) return o.toString();
        else return "None";
    }
}
