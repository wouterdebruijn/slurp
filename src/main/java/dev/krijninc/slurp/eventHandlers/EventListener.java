package dev.krijninc.slurp.eventHandlers;

import com.google.gson.Gson;
import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkPlayer;
import dev.krijninc.slurp.eventHandlers.blockBreakEvents.*;
import dev.krijninc.slurp.helpers.DashboardServerConnector;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.http.HttpResponse;
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
        coalOreEventHandler = new CoalOreEventHandler(2 * modifier, generateChance(0.1, 0.2), generateInt(0, 3));
        copperOreEventHandler = new CopperOreEventHandler(generateAmount(3), generateChance(0.4, 0.6), generateInt(0, 3));
        diamondOreEventHandler = new DiamondOreEventHandler((int) (1 * modifier), generateChance(0.8, 1));
        emeraldOreEventHandler = new EmeraldOreEventHandler(generateAmount(5, 14), generateChance(0.8, 1));
        goldOreEventHandler = new GoldOreEventHandler(generateAmount(6), generateChance(0.4, 0.6), generateInt(0, 3));
        ironOreEventHandler = new IronOreEventHandler(generateAmount(3), generateChance(0.4, 0.6), generateInt(0, 3));
        lapisOreEventHandler = new LapisOreEventHandler(generateAmount(5), generateChance(0.6, 0.8));
        logsEventHandler = new LogsEventHandler(generateAmount(3), generateChance(0.0, 0.1));
        netherGoldOreEventHandler = new NetherGoldOreEventHandler(generateAmount(4), generateChance(0.0, 0.2));
        quartzOreEventHandler = new QuartzOreEventHandler(generateAmount(5), generateChance(0.1, 0.3));
        redstoneOreEventHandler = new RedstoneOreEventHandler(generateAmount(4), generateChance(0.3, 0.5), generateInt(0, 3));
        stoneEventHandler = new StoneEventHandler((int) (1 * modifier), 0.0013);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            Player player = event.getPlayer();
            DrunkPlayer savedDrunkPlayer = Slurp.getDrunkPlayer(player.getUniqueId());

            if (savedDrunkPlayer == null) {
                Slurp.getFancyLogger().info("New Player joined adding to dashboard.");
                HttpResponse<String> response = DashboardServerConnector.post("/player", String.format("{\"uuid\": \"%s\"}", event.getPlayer().getUniqueId()));

                // If the dashboard created a new player, we save that player to the local cache.
                if (response.statusCode() == 200) {
                    Gson gson = new Gson();
                    DrunkPlayer responsePlayer = gson.fromJson(response.body(), DrunkPlayer.class);
                    Slurp.setDrunkPlayer(responsePlayer);
                }
            } else {
                // Show the existing player their scoreboard.
                DrunkPlayer p = Slurp.getDrunkPlayer(player.getUniqueId());
                Slurp.getSidebarManager().createSidebar(p);
            }
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

    // TODO: REFACTOR THESE FUNCTIONS
    private double generateAmount(int max) {
        return generateAmount(2, max);
    }

    private double generateAmount(int min, int max) {
        return (generateInt(min, max) * modifier);
    }

    private int generateInt(int min, int max) {
        return (random.nextInt(max + 1 - min) + min);
    }

    private double generateChance(double min, double max) {
        return random.nextDouble(max - min) + min;
    }
}
