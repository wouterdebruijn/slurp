package nl.wouterdebruijn.slurp.controller;

import nl.wouterdebruijn.slurp.Slurp;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigController {
    public static String getString(String path) {
        FileConfiguration fileConfiguration = Slurp.getPlugin().getConfig();
        return fileConfiguration.getString(path);
    }

    public static int getInt(String path) {
        FileConfiguration fileConfiguration = Slurp.getPlugin().getConfig();
        return fileConfiguration.getInt(path);
    }

    public static double getDouble(String path) {
        FileConfiguration fileConfiguration = Slurp.getPlugin().getConfig();
        return fileConfiguration.getDouble(path);
    }

    public static boolean getBoolean(String path) {
        FileConfiguration fileConfiguration = Slurp.getPlugin().getConfig();
        return fileConfiguration.getBoolean(path);
    }
}
