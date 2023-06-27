package nl.wouterdebruijn.slurp.helper;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.game.Ores;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class SlurpConfig {
    public static void initialize() {
        Plugin plugin = Slurp.plugin;
        setValues();
        setOreValues();
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

    private static void setValues() {
        FileConfiguration conf = Slurp.plugin.getConfig();
        for (ConfigValue cv : ConfigValue.values()) {
            if (!conf.contains(cv.getPath())) {
                conf.set(cv.getPath(), cv.getDefaultValue());
            }
        }
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
     * @param val ConfigValue's data to be stored in the config.
     * @param value Integer value to be stored in the config.
     */
    public static void setValue(ConfigValue val, int value) {
        FileConfiguration conf = Slurp.plugin.getConfig();
        conf.set(val.getPath(), value);
        saveConfig();
    }

    private static void setOreValues() {
        FileConfiguration conf = Slurp.plugin.getConfig();
        for (Ores o : Ores.values()) { // set sip value of each ore to 1.
            if (!conf.contains(o.getPath())) {
                conf.set(o.getPath(), 1);
            }
            if (!conf.contains(o.getChancePath())) {
                conf.set(o.getChancePath(), o.getDefaultChanceValue());
            }
        }
    }

    public static int getOreSipValue(Ores o) {
        FileConfiguration conf = Slurp.plugin.getConfig();
        if (conf.contains(o.getPath())) {
            return conf.getInt(o.getPath());
        }
        return 0;
    }

    public static int getOreChanceValue(Ores o) {
        FileConfiguration conf = Slurp.plugin.getConfig();
        if (conf.contains(o.getChancePath())) {
            return conf.getInt(o.getChancePath());
        }
        return 0;
    }

    public static void saveConfig() {
        Slurp.getPlugin(Slurp.class).saveConfig();
    }
}
