package nl.wouterdebruijn.slurp;

import nl.wouterdebruijn.slurp.commands.*;
import nl.wouterdebruijn.slurp.controller.LogController;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.BlockBreakEventHandler;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.BlockPlaceEventHandler;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.PlayerDeathEventHandler;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockBreakExecutors.*;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockPlaceExecutors.CraftingTableExecutor;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockPlaceExecutors.PlanksExecutor;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockPlaceExecutors.TorchExecutor;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.playerDeathExecutors.*;
import nl.wouterdebruijn.slurp.eventHandlers.utilityEvents.PlayerJoinEventHandler;
import nl.wouterdebruijn.slurp.exceptions.APIPostException;
import nl.wouterdebruijn.slurp.repository.SlurpServerRepository;
import nl.wouterdebruijn.slurp.serverRunnables.DrinkingBuddiesRunnable;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;

public final class Slurp extends JavaPlugin {

    private static Slurp plugin = null;

    public static Slurp getPlugin() {
        return plugin;
    }

    private static void registerEvents() {
        // Create and register new event handler
        BlockBreakEventHandler eventHandler = new BlockBreakEventHandler();

        // Register specific block executors
        eventHandler.registerExecutor(new CoalOreExecutor());
        eventHandler.registerExecutor(new CopperOreExecutor());
        eventHandler.registerExecutor(new DiamondOreExecutor());
        eventHandler.registerExecutor(new EmeraldOreExecutor());
        eventHandler.registerExecutor(new EnchientDebrisExecutor());
        eventHandler.registerExecutor(new GoldOreExecutor());
        eventHandler.registerExecutor(new IronOreExecutor());
        eventHandler.registerExecutor(new LapisOreExecutor());
        eventHandler.registerExecutor(new NetherGoldOreExecutor());
        eventHandler.registerExecutor(new NetherQuartzOreExecutor());
        eventHandler.registerExecutor(new RedstoneOreExecutor());
        eventHandler.registerExecutor(new StoneExecutor());

        BlockPlaceEventHandler blockPlaceEventHandler = new BlockPlaceEventHandler();

        blockPlaceEventHandler.registerExecutor(new TorchExecutor());
        blockPlaceEventHandler.registerExecutor(new CraftingTableExecutor());
        blockPlaceEventHandler.registerExecutor(new PlanksExecutor());

        // Other drinking events
        PlayerDeathEventHandler playerDeathEventHandler = new PlayerDeathEventHandler();
        playerDeathEventHandler.registerExecutor(new PlayerDeathBlazeExecutor());
        playerDeathEventHandler.registerExecutor(new PlayerDeathCreeperExecutor());
        playerDeathEventHandler.registerExecutor(new PlayerDeathPlayerExecutor());
        playerDeathEventHandler.registerExecutor(new PlayerDeathSkeletonExecutor());
        playerDeathEventHandler.registerExecutor(new PlayerDeathSpiderExecutor());
        playerDeathEventHandler.registerExecutor(new PlayerDeathZombieExecutor());

        // Create and register player join utility event.
        new PlayerJoinEventHandler();
    }

    private static void registerCommands() {
        // Create and register player commands.
        new GiveShot();
        new GiveSip();
        new TakeShot();
        new TakeSip();
        new Stats();
        new ConvertShot();
        new ConvertSip();
        new NewDrinkingBuddies();
        new ReloadConfig();
    }

    public static void reload() {
        LogController.info("Reloading config file");
        Slurp.getPlugin().reloadConfig();

        LogController.info("Reloading events");
        HandlerList.unregisterAll();
        registerEvents();

        LogController.info("Plugin config reloaded");
    }

    @Override
    public void onEnable() {
        plugin = this;
        LogController.info("Slurp on enable event running.");

        // Save default config if we don't have one yet.
        Slurp.getPlugin().saveDefaultConfig();

        try {
            SlurpServerRepository.loadFromJSON();

            if (SlurpServerRepository.get() == null) {
                throw new FileNotFoundException();
            }
            LogController.info("Loaded server config");
        } catch (FileNotFoundException e) {
            // If the saved server config couldn't be loaded, register as a new server.
            try {
                SlurpServerRepository.set(SlurpServerRepository.registerServer());
                SlurpServerRepository.saveToJSON();
                LogController.info("Registered Server to SlurpAPI");
            } catch (APIPostException ex) {
                // Display stack trace if we couldn't register the server.
                ex.printStackTrace();
            }
        }

        registerEvents();
        registerCommands();

        // Register the drinking buddies runnable
        DrinkingBuddiesRunnable.registerRunner();
    }
}
