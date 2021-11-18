package dev.krijninc.slurp;

import com.google.gson.Gson;
import dev.krijninc.slurp.entities.DrunkPlayer;
import dev.krijninc.slurp.entities.DrunkPlayerCollection;
import dev.krijninc.slurp.entities.DrunkServer;
import dev.krijninc.slurp.eventHandlers.EventListener;
import dev.krijninc.slurp.exceptions.FetchException;
import dev.krijninc.slurp.helpers.*;
import dev.krijninc.slurp.runnables.ChooseDrinkingBuddies;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Array;
import java.net.http.HttpResponse;
import java.util.*;

public final class Slurp extends JavaPlugin {

    private static JavaPlugin plugin;
    private static DrunkServer server;
    private static FancyLogger fancyLogger;
    private static SidebarManager sidebarManager;
    private static ChooseDrinkingBuddies chooseDrinkingBuddies;

    private static final HashMap<UUID, DrunkPlayer> drunkPlayers = new HashMap<>();

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static FancyLogger getFancyLogger() {
        return fancyLogger;
    }

    public static SidebarManager getSidebarManager() {
        return sidebarManager;
    }

    public static DrunkServer getDrunkServer() {
        return server;
    }

    public static DrunkPlayer getDrunkPlayer(UUID uuid) {
        return drunkPlayers.get(uuid);
    }

    public static HashMap<UUID, DrunkPlayer> getDrunkPlayers() {
        return drunkPlayers;
    }

    public static void setDrunkPlayer(DrunkPlayer player) {
        drunkPlayers.put(player.getUuid(), player);
        sidebarManager.createSidebar(player);
    }

    public static ArrayList<Player> getDrinkingBuddies() {
        return chooseDrinkingBuddies.getDrinkingBuddies();
    }

    public static void broadcastMessage(String message) {
        for (Player player : getPlugin().getServer().getOnlinePlayers()) {
            player.sendMessage(ChatColor.YELLOW + ConfigLoader.getString("slurp-prefix") + message);
        }
    }

    public static void sendMessage(Player player, String message) {
        String messageListString = String.join("", message);
        player.sendMessage(ChatColor.YELLOW + ConfigLoader.getString("slurp-prefix") + messageListString);
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

        // Register sidebar manager
        try {
            sidebarManager = new SidebarManager();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        } catch (FetchException e) {
            fancyLogger.severe("Error while registering server to dashboard service.");
        } catch (Exception e) {
            fancyLogger.severe("Error while saving server to local cache.");
        }

        // Fill the player storage with existing players;
        try {
            HttpResponse<String> response = DashboardServerConnector.get("/player?server=" + server.getUuid());
            Gson gson = new Gson();

            DrunkPlayerCollection drunkPlayerCollection = gson.fromJson(response.body(), DrunkPlayerCollection.class);
            for (DrunkPlayer player : drunkPlayerCollection.players) {
                setDrunkPlayer(player);
            }
        } catch (Exception e) {
            fancyLogger.severe("Could not contact backend server!");
            e.printStackTrace();
        }

        fancyLogger.info("Registering commands to JavaPlugin.");
        CommandLoader.load();

        fancyLogger.info("Registering events to JavaPlugin.");
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        drunkPlayers.forEach((uuid, _drunkPlayer) -> getLogger().info(String.format("Player in list: %s", uuid)));

        if (ConfigLoader.getBoolean("drinking-buddy-enabled")) {
            chooseDrinkingBuddies = new ChooseDrinkingBuddies(ConfigLoader.getInt("drinking-buddy-group-size"));
            chooseDrinkingBuddies.runTaskTimer(this, 0, 20L * ConfigLoader.getInt("drinking-buddy-interval"));
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
