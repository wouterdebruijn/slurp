package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class RedstoneOreEventHandler extends BlockBreakEventHandler {
    public RedstoneOreEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE});
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
    }
}
