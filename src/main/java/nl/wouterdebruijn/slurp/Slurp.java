package nl.wouterdebruijn.slurp;

import nl.wouterdebruijn.slurp.controller.LogController;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.BlockBreakEventHandler;
import nl.wouterdebruijn.slurp.exceptions.APIPostException;
import nl.wouterdebruijn.slurp.repository.SlurpServerRepository;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;

public final class Slurp extends JavaPlugin {

    private static Slurp plugin = null;

    public static Slurp getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        LogController.info("Slurp on enable event running.");

        try {
            SlurpServerRepository.loadFromJSON();
            LogController.info("Loaded server config");
        } catch (FileNotFoundException e) {
            // If the saved server config couldn't be loaded, register as a new server.
            try {
                SlurpServerRepository.set(SlurpServerRepository.registerServer());
                SlurpServerRepository.saveToJSON();
            } catch (APIPostException ex) {
                // Display stack trace if we couldn't register the server.
                ex.printStackTrace();
            }
        }

        new BlockBreakEventHandler();
    }
}