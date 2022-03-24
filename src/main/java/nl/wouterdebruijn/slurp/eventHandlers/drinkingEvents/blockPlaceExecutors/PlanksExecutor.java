package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockPlaceExecutors;

import nl.wouterdebruijn.slurp.controller.ConfigController;
import nl.wouterdebruijn.slurp.controller.MessageController;
import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.BlockPlaceEventExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlanksExecutor extends BlockPlaceEventExecutor {
    public PlanksExecutor() {
        super(generateChange(ConfigController.getDouble("drinking-events.block-place-events.planks-chance")), new Material[]{Material.ACACIA_PLANKS, Material.BIRCH_PLANKS, Material.CRIMSON_PLANKS, Material.OAK_PLANKS, Material.JUNGLE_PLANKS, Material.DARK_OAK_PLANKS, Material.SPRUCE_PLANKS, Material.WARPED_PLANKS});
    }

    @Override
    protected void onExecution(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        SlurpEntry entry = playerDrinkEvent(player.getUniqueId(), 1, 0, false);
        broadcastPlayerDrinking(player.getName(), entry.shots, entry.sips, entry.giveable, "planks");
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
                MessageController.broadcast(true, String.format("%s encountered termites, now they take %d %s and %d %s!", playername, shots, shotName(shots), sips, sipName(sips)));
            } else if (shots >= 1) {
                MessageController.broadcast(true, String.format("%s encountered termites, now they take %d %s!", playername, shots, shotName(shots)));
            } else {
                MessageController.broadcast(true, String.format("%s encountered termites, now they take %d %s!", playername, sips, sipName(sips)));
            }
        } else {
            if (shots >= 1 && sips >= 1) {
                MessageController.broadcast(true, String.format("%s encountered termites, they received %d %s and %d %s to give away!", playername, shots, shotName(shots), sips, sipName(sips)));
            } else if (shots >= 1) {
                MessageController.broadcast(true, String.format("%s encountered termites, they received %d %s to give away!", playername, shots, shotName(shots)));
            } else {
                MessageController.broadcast(true, String.format("%s encountered termites, they received %d %s to give away!", playername, sips, sipName(sips)));
            }
        }
    }
}
