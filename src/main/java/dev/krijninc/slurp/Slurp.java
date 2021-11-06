package dev.krijninc.slurp;

import dev.krijninc.slurp.entities.DrunkServer;
import org.bukkit.plugin.java.JavaPlugin;

public final class Slurp extends JavaPlugin {

    private static JavaPlugin plugin;
    private static DrunkServer server;

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        // Register the server if needed.
        try {
            DrunkServer drunkServer = DrunkServer.loadServer();
            if (drunkServer == null) {
                DrunkServer server = DrunkServer.registerServer();
                Slurp.server = server;
                server.save();
            } else {
                server = drunkServer;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.getLogger().info(server.getUuid().toString());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
