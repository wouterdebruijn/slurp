package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockBreakExecutors;

import nl.wouterdebruijn.slurp.controller.ConfigController;
import nl.wouterdebruijn.slurp.controller.MessageController;
import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.BlockBreakEventExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class StoneExecutor extends BlockBreakEventExecutor {
    public StoneExecutor() {
        super(generateChange(ConfigController.getDouble("drinking-events.block-place-events.planks-chance")), new Material[]{Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.OAK_LOG, Material.JUNGLE_LOG, Material.SPRUCE_LOG});
    }

    @Override
    protected void onExecution(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SlurpEntry entry = serverDrinkingEvent(1, 0, false);
        broadcastPlayerDrinking(player.getName(), entry.shots, entry.sips, entry.giveable, "iron");
    }

    private String shotName(int shots) {
        return shots > 1 ? "shots" : "shot";
    }

    private String sipName(int sips) {
        return sips > 1 ? "sips" : "sip";
    }

    @Override
    protected void broadcastPlayerDrinking(String playername, int shots, int sips, boolean giveable, String orename) {
        if (!giveable) {
            if (shots >= 1 && sips >= 1) {
                MessageController.broadcast(true, String.format("%s found a \u00A7b\"lucy\"\u00A7f stone, now everybody takes %d %s and %d %s!", playername, shots, shotName(shots), sips, sipName(sips)));
            } else if (shots >= 1) {
                MessageController.broadcast(true, String.format("%s found a \u00A7b\"lucy\"\u00A7f stone, now everybody takes %d %s!", playername, shots, shotName(shots)));
            } else {
                MessageController.broadcast(true, String.format("%s found a \u00A7b\"lucy\"\u00A7f stone, now everybody takes %d %s!", playername, sips, sipName(sips)));
            }
        } else {
            if (shots >= 1 && sips >= 1) {
                MessageController.broadcast(true, String.format("%s found a \u00A7b\"lucy\"\u00A7f stone, everybody receives %d %s and %d %s to give away!", playername, shots, shotName(shots), sips, sipName(sips)));
            } else if (shots >= 1) {
                MessageController.broadcast(true, String.format("%s found a \u00A7b\"lucy\"\u00A7f stone, everybody receives %d %s to give away!", playername, shots, shotName(shots)));
            } else {
                MessageController.broadcast(true, String.format("%s found a \u00A7b\"lucy\"\u00A7f stone, everybody receives %d %s to give away!", playername, sips, sipName(sips)));
            }
        }
    }
}
