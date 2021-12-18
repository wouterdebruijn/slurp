package nl.wouterdebruijn.slurp.controller;

import nl.wouterdebruijn.slurp.Slurp;

import java.util.logging.Level;

public class LogController {
    public static void debug(String message) {
        Slurp.getPlugin().getLogger().log(Level.INFO, "[DEBUG] " + message);
    }

    public static void info(String message) {
        Slurp.getPlugin().getLogger().log(Level.INFO, message);
    }

    public static void warning(String message) {
        Slurp.getPlugin().getLogger().log(Level.WARNING, message);
    }

    public static void error(String message) {
        Slurp.getPlugin().getLogger().log(Level.SEVERE, message);
    }
}
