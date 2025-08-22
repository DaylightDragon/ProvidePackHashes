package org.daylight.plugins.provideurlhashes.events;

import static org.daylight.plugins.provideurlhashes.util.common.SimpleLogger.debug;
import static org.daylight.plugins.provideurlhashes.util.common.SimpleLogger.log;

import com.google.common.hash.HashCode;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.daylight.plugins.provideurlhashes.config.ConfigData;
import org.daylight.plugins.provideurlhashes.config.UserData;
import org.daylight.plugins.provideurlhashes.config.UserDataOperations;
import org.daylight.plugins.provideurlhashes.events.custom.PackDownloadedEvent;
import org.daylight.plugins.provideurlhashes.util.PackInjector;

import java.net.URI;

public class MainEventListener implements Listener {
	@EventHandler
    public void onPackDownloaded(PackDownloadedEvent event) {
        if (event.getPackId() != null) {
            PackInjector.injectPackInServer(event.getPackId(), event.getPackUri(), event.getPackHash(), Bukkit.isResourcePackRequired());
            UserData.lastId = event.getPackId();
            UserData.lastUri = URI.create(event.getPackUri().replace("\\", ""));
            UserData.lastHash = HashCode.fromString(event.getPackHash());
            if(ConfigData.saveData) UserDataOperations.saveData();
        }
    }
}
