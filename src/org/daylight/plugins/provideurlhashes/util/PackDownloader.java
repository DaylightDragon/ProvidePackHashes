package org.daylight.plugins.provideurlhashes.util;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.daylight.plugins.provideurlhashes.events.custom.PackDownloadedEvent;
import org.daylight.plugins.provideurlhashes.main.Main;
import org.daylight.plugins.provideurlhashes.util.common.Stash;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class PackDownloader {
    public static void downloadWithHash(JavaPlugin plugin, CommandSender sender, UUID id, String urlString, int maxBytes) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URI uri;
                try {
                    uri = URI.create(urlString.replace("\\", ""));
                } catch (IllegalArgumentException e) {
                    Stash.logSevereAndSend(sender, "Invalid URL: " + e.getMessage());
                    return;
                }
                URL url = uri.toURL();

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(true);

                try (InputStream in = connection.getInputStream()) {
                    long contentLength = connection.getContentLengthLong();
                    if (contentLength > maxBytes) {
                        Stash.logSevereAndSend(sender, "File too large: " + contentLength + " bytes");
                        return;
                    }

                    HashFunction hashFunction = Hashing.sha1();
                    Hasher hasher = hashFunction.newHasher();

                    byte[] buffer = new byte[8196];
                    long total = 0;
                    int read;
                    while ((read = in.read(buffer)) >= 0) {
                        total += read;
                        if (total > maxBytes) {
                            Stash.logSevereAndSend(sender, "File exceeded max size");
                            return;
                        }
                        hasher.putBytes(buffer, 0, read);
                    }

                    HashCode computedHash = hasher.hash();

                    Stash.logAndSend(sender, "Final SHA1 Hash: " + computedHash);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        Bukkit.getPluginManager().callEvent(new PackDownloadedEvent(id.toString(), url.toString(), computedHash.toString())
                                .setSender(sender));
                    });
                }
            } catch (Exception e) {
                Stash.logSevereAndSend(sender, "Download failed");
                if(!(e instanceof IllegalStateException)) e.printStackTrace();
                else Stash.logAndSend(sender, "Most likely due to being interrupted");
            }
        });
    }
}
