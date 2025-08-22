package org.daylight.plugins.provideurlhashes.util;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.daylight.plugins.provideurlhashes.events.custom.PackDownloadedEvent;
import org.daylight.plugins.provideurlhashes.main.Main;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class PackDownloader {
    public static void downloadWithHash(JavaPlugin plugin, UUID id, String urlString, int maxBytes) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URI uri;
                try {
                    uri = URI.create(urlString.replace("\\", ""));
                } catch (IllegalArgumentException e) {
                    Main.plugin.getLogger().severe("Invalid URL: " + e.getMessage());
                    return;
                }
                URL url = uri.toURL();

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(true);

                try (InputStream in = connection.getInputStream()) {
                    long contentLength = connection.getContentLengthLong();
                    if (contentLength > maxBytes) {
                        throw new IOException("File too large: " + contentLength + " bytes");
                    }

                    HashFunction hashFunction = Hashing.sha1();
                    Hasher hasher = hashFunction.newHasher();

                    byte[] buffer = new byte[8196];
                    long total = 0;
                    int read;
                    while ((read = in.read(buffer)) >= 0) {
                        total += read;
                        if (total > maxBytes) throw new IOException("File exceeded max size");
                        hasher.putBytes(buffer, 0, read);
                    }

                    HashCode computedHash = hasher.hash();

                    plugin.getLogger().info("Final SHA1 Hash: " + computedHash);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        Bukkit.getPluginManager().callEvent(new PackDownloadedEvent(id.toString(), url.toString(), computedHash.toString()));
                    });
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Download failed");
                e.printStackTrace();
            }
        });
    }
}
