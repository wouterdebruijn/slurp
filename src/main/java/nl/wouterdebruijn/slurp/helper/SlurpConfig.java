package nl.wouterdebruijn.slurp.helper;

import nl.wouterdebruijn.slurp.Slurp;
import org.bukkit.plugin.Plugin;

public class SlurpConfig {
    public static void initialize() {
        Plugin plugin = Slurp.plugin;
        plugin.saveDefaultConfig();
    }
    private static String getString(String key) {
        String value = Slurp.plugin.getConfig().getString(key);
        if (value == null) {
            throw new NullPointerException("Config key " + key + " is not set.");
        }
        return value;
    }
    public static String apiUrl() {
        return getString("api.url");
    }
    public static String prefix() {
        return getString("chat.prefix");
    }
}
