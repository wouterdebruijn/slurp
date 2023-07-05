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
        if (!conf.contains(ConfigValue.CHANCES_LUCYSTONE.getPath())) { // chances
            conf.set(ConfigValue.CHANCES_LUCYSTONE.getPath(), 1000000);
        } else if (!conf.contains(ConfigValue.CHANCES_BURNING_HANDS.getPath())) {
            conf.set(ConfigValue.CHANCES_BURNING_HANDS.getPath(), 1000000);
        } else if (!conf.contains(ConfigValue.CHANCES_CHOKING.getPath())) {
            conf.set(ConfigValue.CHANCES_CHOKING.getPath(), 1000000);
        } else if (!conf.contains(ConfigValue.CHANCES_FALL_DAMAGE.getPath())) {
            conf.set(ConfigValue.CHANCES_FALL_DAMAGE.getPath(), 1000000);
        } else if (!conf.contains(ConfigValue.CHANCES_SPRINTING.getPath())) {
            conf.set(ConfigValue.CHANCES_SPRINTING.getPath(), 1000000);
        } else if (!conf.contains(ConfigValue.CHANCES_SWIMMING.getPath())) {
            conf.set(ConfigValue.CHANCES_SWIMMING.getPath(), 1000000);
        } else if (!conf.contains(ConfigValue.CHANCES_BOAT.getPath())) {
            conf.set(ConfigValue.CHANCES_BOAT.getPath(), 1000000);
        } else if (!conf.contains(ConfigValue.SIP_LUCYSTONE.getPath())) { // sips
            conf.set(ConfigValue.SIP_LUCYSTONE.getPath(), 0);
        } else if (!conf.contains(ConfigValue.SIP_BURNING_HANDS.getPath())) {
            conf.set(ConfigValue.SIP_BURNING_HANDS.getPath(), 5);
        } else if (!conf.contains(ConfigValue.SIP_CHOKING.getPath())) {
            conf.set(ConfigValue.SIP_CHOKING.getPath(), 4);
        } else if (!conf.contains(ConfigValue.SIP_FALL_DAMAGE.getPath())) {
            conf.set(ConfigValue.SIP_FALL_DAMAGE.getPath(), 2);
        } else if (!conf.contains(ConfigValue.SIP_DIE.getPath())) {
            conf.set(ConfigValue.SIP_DIE.getPath(), 2);
        } else if (!conf.contains(ConfigValue.SIP_KILLER.getPath())) {
            conf.set(ConfigValue.SIP_KILLER.getPath(), 0);
        } else if (!conf.contains(ConfigValue.SIP_KILLED_ANIMAL.getPath())) {
            conf.set(ConfigValue.SIP_KILLED_ANIMAL.getPath(), 3);
        } else if (!conf.contains(ConfigValue.SIP_SPRINTING.getPath())) {
            conf.set(ConfigValue.SIP_SPRINTING.getPath(), 2);
        } else if (!conf.contains(ConfigValue.SIP_SWIMMING.getPath())) {
            conf.set(ConfigValue.SIP_SWIMMING.getPath(), 2);
        } else if (!conf.contains(ConfigValue.SIP_BOAT.getPath())) {
            conf.set(ConfigValue.SIP_BOAT.getPath(), 3);
        } else if (!conf.contains(ConfigValue.SHOT_LUCYSTONE.getPath())) { //shots
            conf.set(ConfigValue.SHOT_LUCYSTONE.getPath(), 1);
        } else if (!conf.contains(ConfigValue.SHOT_BURNING_HANDS.getPath())) {
            conf.set(ConfigValue.SHOT_BURNING_HANDS.getPath(), 0);
        } else if (!conf.contains(ConfigValue.SHOT_CHOKING.getPath())) {
            conf.set(ConfigValue.SHOT_CHOKING.getPath(), 0);
        } else if (!conf.contains(ConfigValue.SHOT_FALL_DAMAGE.getPath())) {
            conf.set(ConfigValue.SHOT_FALL_DAMAGE.getPath(), 0);
        } else if (!conf.contains(ConfigValue.SHOT_DIE.getPath())) {
            conf.set(ConfigValue.SHOT_DIE.getPath(), 0);
        } else if (!conf.contains(ConfigValue.SHOT_KILLER.getPath())) {
            conf.set(ConfigValue.SHOT_KILLER.getPath(), 1);
        } else if (!conf.contains(ConfigValue.SHOT_KILLED_ANIMAL.getPath())) {
            conf.set(ConfigValue.SHOT_KILLED_ANIMAL.getPath(), 0);
        } else if (!conf.contains(ConfigValue.SHOT_SPRINTING.getPath())) {
            conf.set(ConfigValue.SHOT_SPRINTING.getPath(), 0);
        } else if (!conf.contains(ConfigValue.SHOT_SWIMMING.getPath())) {
            conf.set(ConfigValue.SHOT_SWIMMING.getPath(), 0);
        } else if (!conf.contains(ConfigValue.SHOT_BOAT.getPath())) {
            conf.set(ConfigValue.SHOT_BOAT.getPath(), 0);
        }
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

    public static void saveConfig() {
        Slurp.getPlugin(Slurp.class).saveConfig();
    }
}
