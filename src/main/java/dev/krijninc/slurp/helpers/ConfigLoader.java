package dev.krijninc.slurp.helpers;

import dev.krijninc.slurp.Slurp;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigLoader {
    private static FileConfiguration config;

    // TODO: Add enum of command keys that exist.

    public static void saveDefaults() {
        JavaPlugin plugin = Slurp.getPlugin();
        plugin.saveDefaultConfig();
    }

    public static void load() {
        JavaPlugin plugin = Slurp.getPlugin();
        config = plugin.getConfig();
    }

    public static String getString(String key) {
        return config.getString(key);
    }

    public static Boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    public static int getInt(String key) {
        return config.getInt(key);
    }
}
