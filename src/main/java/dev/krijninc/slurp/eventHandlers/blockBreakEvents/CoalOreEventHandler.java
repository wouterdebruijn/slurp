package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class CoalOreEventHandler extends BlockBreakEventHandler {
    public CoalOreEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE});
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
    }
}
