package nl.wouterdebruijn.slurp.helper;

import org.bukkit.configuration.file.FileConfiguration;

import nl.wouterdebruijn.slurp.Slurp;

public class SlurpConfig {
    static FileConfiguration config;

    public static void initialize() {
        // Load the users config file, using the default values for any missing config
        // options
        Slurp.plugin.saveDefaultConfig();
        config = Slurp.plugin.getConfig();
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

    public static String getToken() {
        return getString("api.token");
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
        if (config.contains(val.getPath())) {
            return config.getInt(val.getPath());
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
        config.set(val.getPath(), value);
        saveConfig();
    }

    public static void saveConfig() {
        Slurp.getPlugin(Slurp.class).saveConfig();
    }
}
