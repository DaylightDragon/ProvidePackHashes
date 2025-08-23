package org.daylight.plugins.provideurlhashes.util;

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.packs.ResourcePack;
import org.daylight.plugins.provideurlhashes.util.common.Stash;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public class PackInjector {
    public static void injectPackInServer(CommandSender sender, @NotNull String id, @NotNull String url, String hashCode, boolean required) {
        try {
            ResourcePack packOriginal = Bukkit.getServerResourcePack();
            assert packOriginal != null;

            String asJson = GsonComponentSerializer.gson().serialize(
                    LegacyComponentSerializer.legacySection().deserialize(Bukkit.getResourcePackPrompt()));

            Object properties = extractServerProperties();

            Method fetchPackInfo = properties.getClass()
                    .getDeclaredMethod(
                            "getServerPackInfo",
                            String.class,
                            String.class,
                            String.class,
                            String.class,
                            boolean.class,
                            String.class);
            fetchPackInfo.setAccessible(true);

            // Not possible :(
//            String testHashFromId = Hashing.sha1().hashString(id, StandardCharsets.UTF_8).toString();

            Optional<?> newPackInfo = (Optional<?>) fetchPackInfo.invoke(
                    null,
                    id,
                    url,
                    hashCode,
                    null,
                    required,
                    asJson
            );

            Field infoField = properties.getClass().getDeclaredField("serverResourcePackInfo");
            infoField.setAccessible(true);
            infoField.set(properties, newPackInfo);

            Stash.logAndSend(sender, ChatColor.GREEN + "Successfully replaced the pack with new data");
        } catch (Exception e) {
            e.printStackTrace();
//			throw new RuntimeException("Could not inject resource pack info in server properties.", e);
        }
    }

    private static Object extractServerProperties() throws NoSuchFieldException, IllegalAccessException {
        Object serverInstance = Bukkit.getServer();
        Class<?> parentClass = serverInstance.getClass();
        Field consoleField = parentClass.getDeclaredField("console");
        consoleField.setAccessible(true);
        Object dedicatedServerInstance = consoleField.get(serverInstance);

        Field settingsField = dedicatedServerInstance.getClass().getDeclaredField("settings");
        settingsField.setAccessible(true);
        Object settings = settingsField.get(dedicatedServerInstance);

        Field propsField = settings.getClass().getDeclaredField("properties");
        propsField.setAccessible(true);
        return propsField.get(settings);
    }
}
