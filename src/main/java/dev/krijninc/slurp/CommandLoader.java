package dev.krijninc.slurp;

import dev.krijninc.slurp.commands.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class CommandLoader {

    static void load() {
        JavaPlugin plugin = Slurp.getPlugin();

        Objects.requireNonNull(plugin.getCommand("giveshot")).setExecutor(new GiveShot());
        Objects.requireNonNull(plugin.getCommand("giveSip")).setExecutor(new GiveSip());
        Objects.requireNonNull(plugin.getCommand("stats")).setExecutor(new Stats());
        Objects.requireNonNull(plugin.getCommand("takeshot")).setExecutor(new TakeShot());
        Objects.requireNonNull(plugin.getCommand("takesip")).setExecutor(new TakeSip());
        Objects.requireNonNull(plugin.getCommand("drinkingmode")).setExecutor(new DrinkingMode());
    }
}
