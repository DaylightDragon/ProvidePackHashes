package org.daylight.plugins.provideurlhashes.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

import static org.daylight.plugins.provideurlhashes.util.common.SimpleLogger.log;

public class ConfigUtils {
    public static Object loadKey(YamlConfiguration c, String currentKey, Object defaultValue) {
        return loadKey(c, currentKey, defaultValue, null, null);
    }

    public static Object loadKey(YamlConfiguration c, String currentKey, Object defaultValue, Runnable ifExists, Runnable ifNotExists) {
        try {
            Object currentValue = c.get(currentKey);
//			Object def = null;
            Object returnValue = null;
            if (currentValue == null) {
//				def = getDefaultValue(currentKey);
                c.set(currentKey, defaultValue);
                returnValue = defaultValue;
                if(ifExists!=null) {
                    ifExists.run();
                }
            } else {
                try {
                    returnValue = currentValue;
                    if(ifNotExists!=null) {
                        ifNotExists.run();
                    }
                } catch (ClassCastException e) {
                    logCastEx(currentKey);
                }
            }
//			if(currentKey.equals("settings.a")) {}
            return returnValue;
        } catch (Throwable t) {
            log("Error while reading \"" + currentKey + "\" from the config");
            return defaultValue;
        }
    }

    public static void setKey(YamlConfiguration c, String currentKey, @Nullable Object value) {
        try {
            c.set(currentKey, value);
        } catch (Throwable t) {
            log("Error while writing \"" + currentKey + "\" into the config");
        }
    }

    public static void saveConfig(YamlConfiguration config, File f) {
        try {
            config.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns true if found changes
     */
    public static boolean saveConfigIfHasChanges(YamlConfiguration newConfig, YamlConfiguration oldConfig, File f) {
        boolean hasChanges = false;
        for(String key : newConfig.getKeys(true)) { // missing keys added by user
            if(!oldConfig.contains(key) || ((oldConfig.get(key) != null) && (newConfig.get(key) != null) && !oldConfig.get(key).equals(newConfig.get(key)))) {
                hasChanges = true;
            }
        }
        for(String key : oldConfig.getKeys(true)) {
            if(!newConfig.contains(key)) {
                hasChanges = true;
            }
        }

        if (hasChanges) {
            try {
                newConfig.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hasChanges;
    }

    public static void logCastEx(String currentKey) {
        log("Wrong value type for \"" + currentKey + "\" in the config");
    }
}
