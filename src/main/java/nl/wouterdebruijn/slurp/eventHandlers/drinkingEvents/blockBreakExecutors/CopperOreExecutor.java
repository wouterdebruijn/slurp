package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockBreakExecutors;

import nl.wouterdebruijn.slurp.controller.ConfigController;
import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.BlockBreakEventExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class CopperOreExecutor extends BlockBreakEventExecutor {
    public CopperOreExecutor() {
        super(generateChange(ConfigController.getDouble("drinking-events.block-break-events.copper-chance")), new Material[]{Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE});
    }

    @Override
    protected void onExecution(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SlurpEntry entry = playerDrinkEvent(player.getUniqueId(), 0, 4, true);
        broadcastPlayerDrinking(player.getName(), entry.shots, entry.sips, entry.giveable, "copper");
    }
}
