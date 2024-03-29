package nl.wouterdebruijn.slurp;

import nl.wouterdebruijn.slurp.command.ConfigCmd;
import nl.wouterdebruijn.slurp.command.entry.*;
import nl.wouterdebruijn.slurp.command.session.*;
import nl.wouterdebruijn.slurp.helper.Permissions;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.events.*;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpSessionManager;
import nl.wouterdebruijn.slurp.listener.SlurpSessionSubscriptionListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

public final class Slurp extends JavaPlugin {
    private static final ArrayList<GameEvent> gameEvents = new ArrayList<>();
    public static Plugin plugin = null;
    public static Logger logger = null;

    public static void reload() {
        Slurp.plugin.reloadConfig();
        FileConfiguration config = Slurp.plugin.getConfig();
        gameEvents.forEach(handler -> handler.reload(config));
        Slurp.plugin.saveConfig();
    }

    @Override
    public void onDisable() {
        SlurpSessionManager.saveToDisk();
        SlurpPlayerManager.saveToDisk();
        SlurpConfig.saveConfig();
    }

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();

        SlurpSessionManager.loadFromDisk();
        SlurpPlayerManager.loadFromDisk();

        SlurpConfig.initialize();

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
        Objects.requireNonNull(getCommand("create_entry")).setExecutor(new CreateEntry());
        Objects.requireNonNull(getCommand("leave")).setExecutor(new Leave());

        Objects.requireNonNull(getCommand("giveshot")).setExecutor(new GiveShot());
        Objects.requireNonNull(getCommand("givesip")).setExecutor(new GiveSip());
        Objects.requireNonNull(getCommand("setvalue")).setExecutor(new ConfigCmd());

        Objects.requireNonNull(getCommand("takeshot")).setExecutor(new TakeShot());
        Objects.requireNonNull(getCommand("takesip")).setExecutor(new TakeSip());
        Objects.requireNonNull(getCommand("rockpaperscissors")).setExecutor(new PlayRockPaperScissors());

        Objects.requireNonNull(getCommand("convertshot")).setExecutor(new ConvertShot());
        Objects.requireNonNull(getCommand("convertsip")).setExecutor(new ConvertSip());

        // Register listeners
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new SlurpSessionSubscriptionListener(), this);

        // New GameEvent API
        FileConfiguration config = getConfig();
        gameEvents.add(new FurnaceBurnEvent(config).register(this));
        gameEvents.add(new LucyStoneEvent(config).register(this));
        gameEvents.add(new ChokingPlayerEvent(config).register(this));
        gameEvents.add(new BrokenLegsEvent(config).register(this));
        gameEvents.add(new PlayerKillsEvent(config).register(this));
        gameEvents.add(new PlayerDiesEvent(config).register(this));

        gameEvents.add(new IronOreEvent(config).register(this));
        gameEvents.add(new GoldOreEvent(config).register(this));
        gameEvents.add(new DiamondOreEvent(config).register(this));
        gameEvents.add(new EmeraldOreEvent(config).register(this));
        gameEvents.add(new LapisOreEvent(config).register(this));
        gameEvents.add(new RedstoneOreEvent(config).register(this));
        gameEvents.add(new CoalOreEvent(config).register(this));
        gameEvents.add(new NetherQuartzOreEvent(config).register(this));
        gameEvents.add(new NetheriteOreEvent(config).register(this));

        gameEvents.add(new HostileMobKillEvent(config).register(this));
        gameEvents.add(new PassiveMobKillEvent(config).register(this));

        gameEvents.add(new LogsEvent(config).register(this));
        gameEvents.add(new TorchEvent(config).register(this));

    }
}
