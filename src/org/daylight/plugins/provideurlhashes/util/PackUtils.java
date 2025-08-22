package org.daylight.plugins.provideurlhashes.util;

import org.bukkit.Bukkit;
import org.bukkit.packs.ResourcePack;
import org.daylight.plugins.provideurlhashes.config.UserData;
import org.daylight.plugins.provideurlhashes.main.Main;

import java.net.URI;

/**
 * A class for utility methods for managing resource pack data
 */
public class PackUtils {
    public static void downloadPackIfNeeded() {
        downloadPackIfNeeded(false, null);
    }

    /**
     * Either downloads the pack and sets the new data (only in RAM) or loads the data from saved data
     * @param bypassCheck Whether to skip checks and download anyway
     * @param urlText A specified URL to download from
     */
    public static void downloadPackIfNeeded(boolean bypassCheck, String urlText) {
        if(!bypassCheck && !checkNeededToUpdate()) {
            injectPackDataFromSaved();
            return;
        }
        ResourcePack resourcePack = Bukkit.getServerResourcePack();
        assert resourcePack != null;
        String url = resourcePack.getUrl();
        if(urlText != null) url = urlText;

        Main.plugin.getLogger().info("Downloading the pack");
        PackDownloader.downloadWithHash(Main.getInstance(), resourcePack.getId(), url, 262144000);
    }


    private static void injectPackDataFromSaved() {
        PackInjector.injectPackInServer(UserData.lastId, UserData.lastUri.toString(), UserData.lastHash.toString(), Bukkit.isResourcePackRequired());
    }

    /**
     * Checking conditions for downloading the pack
     * @return True if need to download
     */
    private static boolean checkNeededToUpdate() {
        ResourcePack resourcePack = Bukkit.getServerResourcePack();
        if(resourcePack == null) return false; // no pack
        if(resourcePack.getHash() != null && !resourcePack.getHash().isBlank()) return false; // hash provided
        if(UserData.lastId != null && !UserData.lastId.equals(resourcePack.getId().toString())) return true; // id changed
        if(UserData.lastUri != null && !UserData.lastUri.equals(URI.create(resourcePack.getUrl().replace("\\", "")))) return true; // url changed
        if(UserData.lastHash == null) return true; // hash not saved (and not provided!)
        return false;
    }
}
