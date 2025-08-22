package org.daylight.plugins.provideurlhashes.config;

import static org.daylight.plugins.provideurlhashes.util.common.SimpleLogger.debug;
import static org.daylight.plugins.provideurlhashes.util.common.SimpleLogger.log;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.daylight.plugins.provideurlhashes.data.Lines;
import org.daylight.plugins.provideurlhashes.data.References;

public class ConfigCompatibility {
	public static void updateConfig() {
		File f = getConfigFile();
		f.getParentFile().mkdirs();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		YamlConfiguration cOld = YamlConfiguration.loadConfiguration(f);
		
//		updateConfigFrom1_0To1_1(c);
		
		ConfigUtils.saveConfigIfHasChanges(c, cOld, f);
	}
	
	// Example
	private static void updateConfigFrom1_0To1_1(YamlConfiguration c) {
		String newPrefixString = "new.place";
		String currentKey;
		
		currentKey = "path.to.name";
		moveValue(c, currentKey, newPrefixString + currentKey);
	}
	
	private static File getConfigFile() {
		return new File(References.pluginConfigDirectory, Lines.mainConfigName);
	}
	
	private static void moveValue(YamlConfiguration c, String oldKey, String newKey) {
		if(c.get(newKey)==null && c.get(oldKey)!=null) {
			Object currentValue = null;
			try {
				currentValue = c.get(oldKey);
			} catch (ClassCastException e) {
				
			}
//			if (currentValue!=null) {
				c.set(newKey, currentValue);
//			} else {
//				c.set(newKey, ConfigOperations.getDefaultValue(newKey));
//			}
		}
		c.set(oldKey, null);
	}
}
