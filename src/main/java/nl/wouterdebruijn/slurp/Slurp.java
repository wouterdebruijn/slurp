package nl.wouterdebruijn.slurp;

import java.util.Objects;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import nl.wouterdebruijn.slurp.command.ConfigCmd;
import nl.wouterdebruijn.slurp.command.entry.GiveShot;
import nl.wouterdebruijn.slurp.command.entry.GiveSip;
import nl.wouterdebruijn.slurp.command.entry.TakeShot;
import nl.wouterdebruijn.slurp.command.entry.TakeSip;
import nl.wouterdebruijn.slurp.command.session.ActionGeneratorReset;
import nl.wouterdebruijn.slurp.command.session.Create;
import nl.wouterdebruijn.slurp.command.session.CreateEntry;
import nl.wouterdebruijn.slurp.command.session.Debug;
import nl.wouterdebruijn.slurp.command.session.DrinkingBuddyReset;
import nl.wouterdebruijn.slurp.command.session.Join;
import nl.wouterdebruijn.slurp.command.session.Leave;
import nl.wouterdebruijn.slurp.command.session.PlayRockPaperScissors;
import nl.wouterdebruijn.slurp.command.session.Reload;
import nl.wouterdebruijn.slurp.endpoint.GoogleAI;
import nl.wouterdebruijn.slurp.endpoint.PocketBase;
import nl.wouterdebruijn.slurp.helper.Permissions;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpSessionManager;
import nl.wouterdebruijn.slurp.listener.SlurpSessionSubscriptionListener;

public final class Slurp extends JavaPlugin {
    public static Plugin plugin = null;
    public static Logger logger = null;

    public static void reload() {
        Slurp.plugin.reloadConfig();
        Slurp.plugin.saveConfig();
    }

    @Override
    public void onDisable() {
        SlurpSessionManager.unsubscribe();

        SlurpSessionManager.saveToDisk();
        SlurpPlayerManager.saveToDisk();
    }

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();

        SlurpSessionManager.loadFromDisk();
        SlurpPlayerManager.loadFromDisk();

        // Subscribe to the PocketBase SSE events
        SlurpSessionManager.subscribe();

        SlurpConfig.initialize();
        try {
            PocketBase.authenticate(SlurpConfig.getUsername(), SlurpConfig.getPassword()).thenAccept(a -> {
                logger.info("Successfully authenticated with PocketBase.");
            });
        } catch (Exception e) {
            logger.severe("Failed to authenticate with PocketBase: " + e.getMessage());
            e.printStackTrace();
        }

        GoogleAI.initialize(SlurpConfig.getGoogleAIToken());

        // Register permissions
        for (Permissions permission : Permissions.values()) {
            Bukkit.getPluginManager().addPermission(permission.getBukkitPermission());
        }

        // Register commands
        Objects.requireNonNull(getCommand("join")).setExecutor(new Join());
        Objects.requireNonNull(getCommand("create")).setExecutor(new Create());
        Objects.requireNonNull(getCommand("debug")).setExecutor(new Debug());
        Objects.requireNonNull(getCommand("slurpreload")).setExecutor(new Reload());
        Objects.requireNonNull(getCommand("drinkingbuddyreset")).setExecutor(new DrinkingBuddyReset());
        Objects.requireNonNull(getCommand("actiongeneratorreset")).setExecutor(new ActionGeneratorReset());
        Objects.requireNonNull(getCommand("create_entry")).setExecutor(new CreateEntry());
        Objects.requireNonNull(getCommand("leave")).setExecutor(new Leave());

        Objects.requireNonNull(getCommand("setvalue")).setExecutor(new ConfigCmd());

        Objects.requireNonNull(getCommand("givesip")).setExecutor(new GiveSip());
        Objects.requireNonNull(getCommand("takesip")).setExecutor(new TakeSip());

        Objects.requireNonNull(getCommand("giveshot")).setExecutor(new GiveShot());
        Objects.requireNonNull(getCommand("takeshot")).setExecutor(new TakeShot());

        Objects.requireNonNull(getCommand("rockpaperscissors")).setExecutor(new PlayRockPaperScissors());

        // Register listeners
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new SlurpSessionSubscriptionListener(), this);
    }
}
