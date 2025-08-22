package org.daylight.plugins.provideurlhashes.config;

import static org.daylight.plugins.provideurlhashes.util.common.SimpleLogger.debug;
import static org.daylight.plugins.provideurlhashes.util.common.SimpleLogger.log;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.daylight.plugins.provideurlhashes.data.References;
import org.daylight.plugins.provideurlhashes.main.Main;
	
public class ConfigWatcher {
	public WatchService ws;
	private Thread configWatchingThread;
	private long lastTimeUpdated = 0L;

	public ConfigWatcher() {
		try {
			ws = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			References.pluginConfigDirectory.toPath().register(ws, StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		if((configWatchingThread!=null && (configWatchingThread.isInterrupted() || !configWatchingThread.isAlive()))
//				|| (configLoadingThread!=null && (configLoadingThread.isInterrupted() || !configLoadingThread.isAlive()))
				) stop();
		configWatchingThread = new Thread(watchingTask);
		configWatchingThread.start();
	}
	
	public void stop() {
		configWatchingThread.interrupt();
	}
	
	private Runnable watchingTask = new Runnable() {
		@Override
		public void run() {
			WatchKey key;
	        try {
				while ((key = ws.take()) != null) {
				    for (WatchEvent<?> event : key.pollEvents()) {
				    	try {
				    		if(lastTimeUpdated != 0L && (System.currentTimeMillis() - lastTimeUpdated <= 20)) continue;
					    	if(event.context().toString().equalsIgnoreCase("main_config.yml")) {
					    		Bukkit.getScheduler().runTask(Main.plugin, ConfigOperations::updateAndLoadConfig);
					    		lastTimeUpdated = System.currentTimeMillis();
					    	}
				    	} catch (IllegalPluginAccessException e) {}
				    }
				    key.reset();
				}
			} catch (InterruptedException e) {}
		}
	};
}
