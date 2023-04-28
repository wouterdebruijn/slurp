package nl.wouterdebruijn.slurp;

import nl.wouterdebruijn.slurp.command.session.*;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpSessionManager;
import nl.wouterdebruijn.slurp.listener.SlurpSessionSubscriptionListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public final class Slurp extends JavaPlugin {
    public static Plugin plugin = null;
    public static Logger logger = null;

    @Override
    public void onDisable() {
        SlurpSessionManager.saveToDisk();
        SlurpPlayerManager.saveToDisk();
    }

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();

        SlurpSessionManager.loadFromDisk();
        SlurpPlayerManager.loadFromDisk();

        SlurpConfig.initialize();

//      Register commands
        Objects.requireNonNull(getCommand("join")).setExecutor(new Join());
        Objects.requireNonNull(getCommand("create")).setExecutor(new Create());
        Objects.requireNonNull(getCommand("debug")).setExecutor(new Debug());
        Objects.requireNonNull(getCommand("create_entry")).setExecutor(new CreateEntry());
        Objects.requireNonNull(getCommand("leave")).setExecutor(new Leave());

//      Register listeners
        getServer().getPluginManager().registerEvents(new SlurpSessionSubscriptionListener(), this);
    }
}
