package nl.wouterdebruijn.slurp.helper;

import nl.wouterdebruijn.slurp.Slurp;
import org.bukkit.configuration.file.FileConfiguration;

public class SlurpConfig {
    public static void initialize() {
        Slurp.plugin.saveDefaultConfig();
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

    /**
     * Returns the value from the config. Only applicable to Chance, Sips or Shots
     *
     * @param val ConfigValue's data to be retrieved from the config.
     * @return Returns integer value stored in config.
     */
    public static int getValue(ConfigValue val) {
        FileConfiguration conf = Slurp.plugin.getConfig();
        if (conf.contains(val.getPath())) {
            return conf.getInt(val.getPath());
        }
        return 0;
    }

    /**
     * Set a value in the config. Only applicable to Chance, Sips or Shots
     *
     * @param val   ConfigValue's data to be stored in the config.
     * @param value Integer value to be stored in the config.
     */
    public static void setValue(ConfigValue val, int value) {
        FileConfiguration conf = Slurp.plugin.getConfig();
        conf.set(val.getPath(), value);
        saveConfig();
    }

    public static void saveConfig() {
        Slurp.getPlugin(Slurp.class).saveConfig();
    }
}
