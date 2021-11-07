package dev.krijninc.slurp;

import dev.krijninc.slurp.entities.DrunkPlayer;
import dev.krijninc.slurp.entities.DrunkServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class Slurp extends JavaPlugin {

    private static JavaPlugin plugin;
    private static DrunkServer server;
    private static FancyLogger fancyLogger;
    private static final HashMap<UUID, DrunkPlayer> drunkPlayers = new HashMap<>();

    public static JavaPlugin getPlugin() {
        return plugin;
    }
    public static FancyLogger getFancyLogger() {
        return fancyLogger;
    }
    public static DrunkServer getDrunkServer() { return server; }
    public static DrunkPlayer getDrunkPlayer(UUID uuid) {
        return drunkPlayers.get(uuid);
    }
    public static void setDrunkPlayer(DrunkPlayer player) {
        drunkPlayers.put(player.getUuid(), player);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        fancyLogger = new FancyLogger(this.getLogger());

        // Load (and maybe generate) the configuration file
        fancyLogger.info("Loading configuration file.");
        ConfigLoader.saveDefaults();
        ConfigLoader.load();

        // Register the server to the dashboard backend, if needed.
        try {
            DrunkServer drunkServer = DrunkServer.loadServer();
            if (drunkServer == null) {
                fancyLogger.info("Registering server to dashboard backend.");
                DrunkServer server = DrunkServer.registerServer();
                Slurp.server = server;
                server.save();
                fancyLogger.success("Server successfully registered to dashboard backend.");
            } else {
                fancyLogger.info("Using existing uuid and access token for dashboard backend.");
                server = drunkServer;
            }
        } catch (Exception e) {
            fancyLogger.severe("Error while registering server to dashboard service.");
            e.printStackTrace();
        }

        fancyLogger.info("Registering commands to JavaPlugin.");
        CommandLoader.load();

        fancyLogger.info("Registering events to JavaPlugin.");
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
