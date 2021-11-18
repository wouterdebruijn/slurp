package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class CopperOreEventHandler extends BlockBreakEventHandler {
    public CopperOreEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE});
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
    }
}
