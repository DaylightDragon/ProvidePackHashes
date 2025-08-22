package org.daylight.plugins.provideurlhashes.main;

import static org.daylight.plugins.provideurlhashes.data.References.configsDirectory;
import static org.daylight.plugins.provideurlhashes.data.References.pluginConfigDirectory;
import static org.daylight.plugins.provideurlhashes.data.References.serverDirectory;
import static org.daylight.plugins.provideurlhashes.data.References.dataDirectory;
//import static org.daylight.plugins.provideurlhashes.data.References.cacheDirectory;
import static org.daylight.plugins.provideurlhashes.util.common.SimpleLogger.debug;
import static org.daylight.plugins.provideurlhashes.util.common.SimpleLogger.log;
import static org.daylight.plugins.provideurlhashes.util.common.Stash.isSenderAPlayerOrConsole;
import static org.daylight.plugins.provideurlhashes.util.common.Stash.isSenderConsole;

import java.io.File;
import java.util.Objects;

import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.daylight.plugins.provideurlhashes.commands.MainCommands;
import org.daylight.plugins.provideurlhashes.commands.completers.plain.EmptyCompleter;
import org.daylight.plugins.provideurlhashes.config.*;
import org.daylight.plugins.provideurlhashes.data.References;
import org.daylight.plugins.provideurlhashes.events.MainEventListener;
import org.daylight.plugins.provideurlhashes.util.PackUtils;
import org.daylight.plugins.provideurlhashes.util.common.SimpleLogger;
import org.daylight.plugins.provideurlhashes.util.common.DebugModeSwitch;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin implements Listener {
	public static Plugin plugin;

	private static Main instance;
	public static Main getInstance() { // not synchronized, but that's ok // what???
		return instance;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		plugin = this;
		initEverything();
		Bukkit.getScheduler().runTask(this, () -> {
			announceFinishedInitialization();
			PackUtils.downloadPackIfNeeded();
		});
	}
	
	@Override
	public void onDisable() {
		log("Provide Pack Hashes plugin has been disabled");
	}
	
	public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String commandLabel, String[] args){
		if (cmd.getName().equalsIgnoreCase("urlHashesToggleSaving")) {
			if(!isSenderAPlayerOrConsole(sender)) return true;
			return MainCommands.toggleSaving(sender, args);
		} else if (cmd.getName().equalsIgnoreCase("urlHashesUpdateHash")) {
			if(!isSenderAPlayerOrConsole(sender)) return true;
			return MainCommands.updateHash(sender, args);
		} else if (cmd.getName().equalsIgnoreCase("urlHashesState")) {
			if(!isSenderAPlayerOrConsole(sender)) return true;
			return MainCommands.printState(sender, args);
		} else if (cmd.getName().equalsIgnoreCase("urlHashesChangePackUrl")) {
			if((ConfigData.allowOnlyConsoleChangePackUrl && !isSenderConsole(sender)) || (!isSenderAPlayerOrConsole(sender))) return true;
			return MainCommands.setNewPackUrl(sender, args);
		}
		return false;
	}
	
	private void initEverything() {
		// Main info

		// Files
		initFiles();
		DebugModeSwitch.init();
		
		// Config and data
		ConfigOperations.updateAndLoadConfig();
		startConfigWatcher();
		UserDataOperations.updateAndLoadData();
		
		// Bukkit API
		registerListeners();
		registerCommands();
		
		// Plugin Functionality
	}
	
	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(new MainEventListener(), this);
	}
	
	private void registerCommands() {
		Objects.requireNonNull(getCommand("urlHashesToggleSaving")).setTabCompleter(new EmptyCompleter());
		Objects.requireNonNull(getCommand("urlHashesUpdateHash")).setTabCompleter(new EmptyCompleter());
		Objects.requireNonNull(getCommand("urlHashesState")).setTabCompleter(new EmptyCompleter());
		Objects.requireNonNull(getCommand("urlHashesChangePackUrl")).setTabCompleter(new EmptyCompleter());
	}
	
	private void initFiles() {
		serverDirectory = Bukkit.getWorldContainer();
		dataDirectory = new File(serverDirectory, "plugins/data/ProvidePackHashes");
//		cacheDirectory = new File(dataDirectory, "cache");
		dataDirectory.mkdirs();

		configsDirectory = new File(serverDirectory, "plugins/config");
		pluginConfigDirectory = new File(configsDirectory, "ProvidePackHashes");
		pluginConfigDirectory.mkdirs();
	}
	
	private void startConfigWatcher() {
		References.configWatcher = new ConfigWatcher();
		References.configWatcher.start();
	}
	
	private void announceFinishedInitialization() {
		log(ChatColor.GREEN + "Provide Pack Hashes" + ChatColor.RESET + " plugin has been enabled!");
		if(SimpleLogger.debugOutputEnabled) debug(ChatColor.AQUA + "Debug mode is enabled");
	}
}