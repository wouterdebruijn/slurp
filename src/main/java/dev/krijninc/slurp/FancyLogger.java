package dev.krijninc.slurp;

import org.bukkit.ChatColor;

import java.util.logging.Logger;

public class FancyLogger {
    private final Logger logger;

    protected FancyLogger(Logger logger) {
        this.logger = logger;
    }

    public void warning(String msg) {
        logger.warning(ChatColor.RED + msg);
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void success(String msg) {
        logger.info(ChatColor.GREEN + msg);
    }

    public void severe(String msg) {
        logger.severe(ChatColor.DARK_RED + msg);
    }
}
