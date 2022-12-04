package nl.wouterdebruijn.slurp.helpers;

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

    public static String ApiUrl() {
        Plugin plugin = Slurp.plugin;
        return getString("api.url");
    }
}
