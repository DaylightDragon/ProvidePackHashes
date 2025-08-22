package org.daylight.plugins.provideurlhashes.config;

import static org.daylight.plugins.provideurlhashes.data.References.pluginConfigDirectory;
import static org.daylight.plugins.provideurlhashes.util.common.SimpleLogger.debug;
import static org.daylight.plugins.provideurlhashes.util.common.SimpleLogger.log;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.daylight.plugins.provideurlhashes.data.Lines;

public class ConfigOperations {
	public static void updateAndLoadConfig() {
		ConfigCompatibility.updateConfig();
		loadConfig();
	}

	private static String currentVersion = "1.0";
	
	public static void loadConfig() {
		File f = new File(pluginConfigDirectory, Lines.mainConfigName);
		@SuppressWarnings("unused")
		boolean fileExisted = f.exists(); // could be improved
		
		f.getParentFile().mkdirs();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		YamlConfiguration cOld = YamlConfiguration.loadConfiguration(f);

		String configVersion = null;

		ConfigData.saveData = (boolean) ConfigUtils.loadKey(c, "settings.data.save_hashes", ConfigData.saveData);
		ConfigData.allowOnlyConsoleChangePackUrl = (boolean) ConfigUtils.loadKey(c, "settings.security.let_only_console_change_pack_url", ConfigData.allowOnlyConsoleChangePackUrl);
		
		configVersion = (String) ConfigUtils.loadKey(c, "internal.config-version", null);
		
		if(configVersion != null) {
//			if(configVersion.equals("1.7")) updateConfig_1_7_to_1_8(c); // in the future
		} else {
			ConfigUtils.loadKey(c, "internal.config-version", currentVersion);
		}

		ConfigUtils.saveConfigIfHasChanges(c, cOld, f);
	}

	public static void saveConfig() {
		File f = new File(pluginConfigDirectory, Lines.mainConfigName);
		@SuppressWarnings("unused")
		boolean fileExisted = f.exists(); // could be improved

		f.getParentFile().mkdirs();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		YamlConfiguration cOld = YamlConfiguration.loadConfiguration(f);

		ConfigUtils.setKey(c, "settings.data.save_hashes", ConfigData.saveData);
		ConfigUtils.setKey(c, "settings.security.let_only_console_change_pack_url", ConfigData.allowOnlyConsoleChangePackUrl);

		ConfigUtils.setKey(c, "internal.config-version", currentVersion);

		ConfigUtils.saveConfigIfHasChanges(c, cOld, f);
	}
}
