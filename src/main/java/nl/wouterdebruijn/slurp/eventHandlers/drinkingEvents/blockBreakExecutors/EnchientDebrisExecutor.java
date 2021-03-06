package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockBreakExecutors;

import nl.wouterdebruijn.slurp.controller.ConfigController;
import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.BlockBreakEventExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class EnchientDebrisExecutor extends BlockBreakEventExecutor {
    public EnchientDebrisExecutor() {
        super(generateChange(ConfigController.getDouble("drinking-events.block-break-events.enchientdebris-chance")), new Material[]{Material.ANCIENT_DEBRIS});
    }

    @Override
    protected void onExecution(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SlurpEntry entry = serverDrinkingEvent(1, 0, true);
        broadcastServerDrinking(player.getName(), entry.shots, entry.sips, entry.giveable, "anchient debris");

    }
}
