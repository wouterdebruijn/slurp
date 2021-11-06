package dev.krijninc.slurp;

import dev.krijninc.slurp.eventHandlers.blockBreakEvents.*;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;

public class EventListener implements Listener {

    CoalOreEventHandler coalOreEventHandler;
    CopperOreEventHandler copperOreEventHandler;
    DiamondOreEventHandler diamondOreEventHandler;
    EmeraldOreEventHandler emeraldOreEventHandler;
    GoldOreEventHandler goldOreEventHandler;
    IronOreEventHandler ironOreEventHandler;
    LapisOreEventHandler lapisOreEventHandler;
    LogsEventHandler logsEventHandler;
    NetherGoldOreEventHandler netherGoldOreEventHandler;
    QuartzOreEventHandler quartzOreEventHandler;
    RedstoneOreEventHandler redstoneOreEventHandler;
    StoneEventHandler stoneEventHandler;

    Random random = new Random(Slurp.getDrunkServer().getSeed());
    double modifier = Slurp.getDrunkServer().getModifier();

    public EventListener() {
        coalOreEventHandler = new CoalOreEventHandler(generateAmount(2), generateChance(0.1, 0.2));
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

        if (blockType == Material.IRON_ORE || blockType == Material.DEEPSLATE_IRON_ORE) {
            ironOreEventHandler.execute(event);
        } else if (blockType == Material.GOLD_ORE || blockType == Material.DEEPSLATE_GOLD_ORE) {
            goldOreEventHandler.execute(event);
        }
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
