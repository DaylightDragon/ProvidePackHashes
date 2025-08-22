package org.daylight.plugins.provideurlhashes.util;

import com.google.common.hash.HashCode;
import org.bukkit.Bukkit;
import org.bukkit.packs.ResourcePack;
import org.daylight.plugins.provideurlhashes.config.UserData;
import org.daylight.plugins.provideurlhashes.main.Main;

import java.net.URI;

public class PackUtils {
    public static void downloadPackIfNeeded() {
        downloadPackIfNeeded(false, null);
    }

    public static void downloadPackIfNeeded(boolean bypassCheck, String urlText) {
        if(!bypassCheck && !checkNeededToUpdate()) {
            injectPackData();
            return;
        }
        ResourcePack resourcePack = Bukkit.getServerResourcePack();
        assert resourcePack != null;
        String url = resourcePack.getUrl();
        if(urlText != null) url = urlText;

        Main.plugin.getLogger().info("Downloading the pack");
        PackDownloader.downloadWithHash(Main.getInstance(), resourcePack.getId(), url, 262144000);
    }

    private static void injectPackData() {
        PackInjector.injectPackInServer(UserData.lastId, UserData.lastUri.toString(), UserData.lastHash.toString(), Bukkit.isResourcePackRequired());
    }

    private static boolean checkNeededToUpdate() {
        ResourcePack resourcePack = Bukkit.getServerResourcePack();
        if(resourcePack == null) return false; // no pack
        if(resourcePack.getHash() != null && !resourcePack.getHash().isBlank()) return false; // hash provided
        if(UserData.lastId != null && !UserData.lastId.equals(resourcePack.getId().toString())) return true;
        if(UserData.lastUri != null && !UserData.lastUri.equals(URI.create(resourcePack.getUrl().replace("\\", "")))) return true;
        if(UserData.lastHash == null) return true;
        return false;
    }
}
