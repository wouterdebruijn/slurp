package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockBreakExecutors;

import nl.wouterdebruijn.slurp.controller.ConfigController;
import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.BlockBreakEventExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class EmeraldOreExecutor extends BlockBreakEventExecutor {
    public EmeraldOreExecutor() {
        super(generateChange(ConfigController.getDouble("drinking-events.block-break-events.emerald-chance")), new Material[]{Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE});
    }

    @Override
    protected void onExecution(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SlurpEntry entry = serverDrinkingEvent(1, 0, false);
        broadcastServerDrinking(player.getName(), entry.shots, entry.sips, entry.giveable, "emeralds");
    }
}
