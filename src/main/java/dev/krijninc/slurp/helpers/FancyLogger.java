package dev.krijninc.slurp.helpers;

import org.bukkit.ChatColor;

import java.util.logging.Logger;

public class FancyLogger {
    private final Logger logger;
    private final boolean enableInfo;

    public FancyLogger(Logger logger, boolean enableInfo) {
        this.logger = logger;
        this.enableInfo = enableInfo;
    }

    public void warning(String msg) {
        logger.warning(ChatColor.RED + msg);
    }

    public void info(String msg) {
        if (enableInfo)
        logger.info(msg);
    }

    public void success(String msg) {
        logger.info(ChatColor.GREEN + msg);
    }

    public void severe(String msg) {
        logger.severe(ChatColor.DARK_RED + msg);
    }
}
