package dev.krijninc.slurp.eventHandlers;

import dev.krijninc.slurp.helpers.DashboardServerConnector;
import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.eventHandlers.blockBreakEvents.*;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;

public class EventListener implements Listener {

    private final CoalOreEventHandler coalOreEventHandler;
    private final CopperOreEventHandler copperOreEventHandler;
    private final DiamondOreEventHandler diamondOreEventHandler;
    private final EmeraldOreEventHandler emeraldOreEventHandler;
    private final GoldOreEventHandler goldOreEventHandler;
    private final IronOreEventHandler ironOreEventHandler;
    private final LapisOreEventHandler lapisOreEventHandler;
    private final LogsEventHandler logsEventHandler;
    private final NetherGoldOreEventHandler netherGoldOreEventHandler;
    private final QuartzOreEventHandler quartzOreEventHandler;
    private final RedstoneOreEventHandler redstoneOreEventHandler;
    private final StoneEventHandler stoneEventHandler;

    Random random = new Random(Slurp.getDrunkServer().getSeed());
    double modifier = Slurp.getDrunkServer().getModifier();

    public EventListener() {
        coalOreEventHandler = new CoalOreEventHandler(2 * modifier, generateChance(0.1, 0.2));
        copperOreEventHandler = new CopperOreEventHandler(generateAmount(3), generateChance(0.4, 0.6));
        diamondOreEventHandler = new DiamondOreEventHandler((int) (1 * modifier) , generateChance(0.8, 1));
        emeraldOreEventHandler = new EmeraldOreEventHandler(generateAmount(5, 14), generateChance(0.8, 1));
        goldOreEventHandler = new GoldOreEventHandler(generateAmount(4), generateChance(0.4, 0.6));
        ironOreEventHandler = new IronOreEventHandler(generateAmount(3), generateChance(0.4, 0.6));
        lapisOreEventHandler = new LapisOreEventHandler(generateAmount(4), generateChance(0.6, 0.8));
        logsEventHandler = new LogsEventHandler(generateAmount(3), generateChance(0.0, 0.1));
        netherGoldOreEventHandler = new NetherGoldOreEventHandler(generateAmount(4), generateChance(0.0, 0.2));
        quartzOreEventHandler = new QuartzOreEventHandler(generateAmount(5), generateChance(0.1, 0.3));
        redstoneOreEventHandler = new RedstoneOreEventHandler(generateAmount(4), generateChance(0.3, 0.5));
        stoneEventHandler = new StoneEventHandler((int) (1 * modifier), 0.0013);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            Slurp.getFancyLogger().info("Player joined adding player to the dashboard");
            DashboardServerConnector.post("/player", String.format("{\"uuid\": \"%s\"}", event.getPlayer().getUniqueId()));
        } catch (Exception e) {
            e.printStackTrace();
            Slurp.getFancyLogger().severe("Could not register new player on dashboard backend!");
        }
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {
        Material blockType = event.getBlock().getType();

        // Execute all the handlers. They have material checking. TODO: For loop this stuff.
        coalOreEventHandler.execute(event, blockType);
        copperOreEventHandler.execute(event, blockType);
        diamondOreEventHandler.execute(event, blockType);
        emeraldOreEventHandler.execute(event, blockType);
        goldOreEventHandler.execute(event, blockType);
        ironOreEventHandler.execute(event, blockType);
        lapisOreEventHandler.execute(event, blockType);
        logsEventHandler.execute(event, blockType);
        netherGoldOreEventHandler.execute(event, blockType);
        quartzOreEventHandler.execute(event, blockType);
        redstoneOreEventHandler.execute(event, blockType);
        stoneEventHandler.execute(event, blockType);
    }

    private double generateAmount(int max) {
        return generateAmount(2, max);
    }

    private double generateAmount(int min, int max) {
        return (random.nextInt(max + 1 - min) + min * modifier);
    }

    private double generateChance(double min, double max) {
        return random.nextDouble(max - min) + min;
    }
}
