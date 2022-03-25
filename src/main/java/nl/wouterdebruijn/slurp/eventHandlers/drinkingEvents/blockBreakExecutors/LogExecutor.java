package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockBreakExecutors;

import nl.wouterdebruijn.slurp.controller.ConfigController;
import nl.wouterdebruijn.slurp.controller.MessageController;
import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.BlockBreakEventExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class LogExecutor extends BlockBreakEventExecutor {
    public LogExecutor() {
        super(generateChange(ConfigController.getDouble("drinking-events.block-break-events.stone-chance")), new Material[]{Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE});
    }

    @Override
    protected void onExecution(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SlurpEntry entry = playerDrinkEvent(player.getUniqueId(), 1, 0, true);
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
                MessageController.broadcast(true, String.format("%s got attacked by termites, now they take %d %s and %d %s!", playername, shots, shotName(shots), sips, sipName(sips)));
            } else if (shots >= 1) {
                MessageController.broadcast(true, String.format("%s got attacked by termites, now they take %d %s!", playername, shots, shotName(shots)));
            } else {
                MessageController.broadcast(true, String.format("%s got attacked by termites, now they take %d %s!", playername, sips, sipName(sips)));
            }
        } else {
            if (shots >= 1 && sips >= 1) {
                MessageController.broadcast(true, String.format("%s got attacked by termites, they received %d %s and %d %s to give away!", playername, shots, shotName(shots), sips, sipName(sips)));
            } else if (shots >= 1) {
                MessageController.broadcast(true, String.format("%s got attacked by termites, they received %d %s to give away!", playername, shots, shotName(shots)));
            } else {
                MessageController.broadcast(true, String.format("%s got attacked by termites, they received %d %s to give away!", playername, sips, sipName(sips)));
            }
        }
    }
}
