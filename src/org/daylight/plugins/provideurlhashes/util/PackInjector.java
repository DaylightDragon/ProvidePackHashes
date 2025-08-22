package org.daylight.plugins.provideurlhashes.util;

import com.google.common.hash.Hashing;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.packs.ResourcePack;
import org.daylight.plugins.provideurlhashes.main.Main;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class PackInjector {
    public static void injectPackInServer(@NotNull String id, @NotNull String url, String hashCode, boolean required) {
        try {
            ResourcePack packOriginal = Bukkit.getServerResourcePack();
            assert packOriginal != null;

            String asJson = GsonComponentSerializer.gson().serialize(
                    LegacyComponentSerializer.legacySection().deserialize(Bukkit.getResourcePackPrompt()));

            Object properties = extractProperties();

            Class<?> propertiesClass = properties.getClass();
            Method getPackInfo = propertiesClass.getDeclaredMethod(
                    "getServerPackInfo",
                    String.class, String.class, String.class, String.class, boolean.class, String.class
            );
            getPackInfo.setAccessible(true);

            // Not possible :(
//            String testHashFromId = Hashing.sha1().hashString(id, StandardCharsets.UTF_8).toString();

            Optional<?> newInfo = (Optional<?>) getPackInfo.invoke(
                    null,
                    id,
                    url,
                    hashCode,
                    null,
                    required,
                    asJson
            );

            Field infoField = propertiesClass.getDeclaredField("serverResourcePackInfo");
            infoField.setAccessible(true);
            infoField.set(properties, newInfo);

            Main.plugin.getLogger().info("Successfully replaced the pack with new data");
        } catch (Exception e) {
            e.printStackTrace();
//			throw new RuntimeException("Could not inject resource pack info in server properties.", e);
        }
    }

    private static Object extractProperties() throws NoSuchFieldException, IllegalAccessException {
        Object parent = Bukkit.getServer();
        Class<?> parentClass = parent.getClass();
        Field dedicatedServerField = parentClass.getDeclaredField("console");
        dedicatedServerField.setAccessible(true);
        Object dedicatedServer = dedicatedServerField.get(parent);

        Class<?> serverClass = dedicatedServer.getClass();
        Field settingsField = serverClass.getDeclaredField("settings");
        settingsField.setAccessible(true);
        Object settings = settingsField.get(dedicatedServer);

        Class<?> settingsClass = settings.getClass();
        Field propertiesField = settingsClass.getDeclaredField("properties");
        propertiesField.setAccessible(true);
        Object properties = propertiesField.get(settings);
        return properties;
    }
}
