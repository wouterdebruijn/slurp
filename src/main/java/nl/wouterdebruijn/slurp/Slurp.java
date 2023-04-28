package nl.wouterdebruijn.slurp;

import nl.wouterdebruijn.slurp.command.session.Create;
import nl.wouterdebruijn.slurp.command.session.Join;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpSessionManager;
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
        Objects.requireNonNull(getCommand("debug")).setExecutor(new nl.wouterdebruijn.slurp.command.session.Debug());
        Objects.requireNonNull(getCommand("create_entry")).setExecutor(new nl.wouterdebruijn.slurp.command.session.CreateEntry());
        Objects.requireNonNull(getCommand("leave")).setExecutor(new nl.wouterdebruijn.slurp.command.session.Leave());
    }
}
