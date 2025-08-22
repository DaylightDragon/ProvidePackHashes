package org.daylight.plugins.provideurlhashes.config;

import com.google.common.hash.HashCode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.daylight.plugins.provideurlhashes.data.Lines;

import java.io.File;
import java.net.URI;

import static org.daylight.plugins.provideurlhashes.data.References.dataDirectory;

public class UserDataOperations {
	public static void updateAndLoadData() {
		// update here
		if(ConfigData.saveData) loadData();
	}

	private static String currentVersion = "1.0";

	public static void loadData() {
		@SuppressWarnings("unused")
		File f = new File(dataDirectory, Lines.mainDataFileName);
		f.getParentFile().mkdirs();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

		UserData.lastId = (String) ConfigUtils.loadKey(c, "data.saving.last_id", UserData.lastId);
		String lastUriText = (String) ConfigUtils.loadKey(c, "data.saving.last_uri", UserData.lastUri);
		if(lastUriText != null) UserData.lastUri = URI.create(lastUriText.replace("\\", ""));
		String hashCodeText = (String) ConfigUtils.loadKey(c, "data.saving.last_hash", UserData.lastHash);
		if(hashCodeText != null) UserData.lastHash = HashCode.fromString(hashCodeText);

		String configVersion = (String) ConfigUtils.loadKey(c, "internal.config-version", null);
		if(configVersion != null) {
			// nothing
		} else {
			ConfigUtils.loadKey(c, "internal.config-version", currentVersion);
		}

		ConfigUtils.saveConfig(c, f);
	}

	public static void saveData() {
		@SuppressWarnings("unused")
		File f = new File(dataDirectory, Lines.mainDataFileName);
		f.getParentFile().mkdirs();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

		ConfigUtils.setKey(c, "data.saving.last_id", UserData.lastId);
		ConfigUtils.setKey(c, "data.saving.last_uri", UserData.lastUri.toString());
		ConfigUtils.setKey(c, "data.saving.last_hash", UserData.lastHash.toString());

		ConfigUtils.setKey(c, "internal.config-version", currentVersion);

		ConfigUtils.saveConfig(c, f);
	}
}
