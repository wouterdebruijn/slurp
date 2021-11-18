package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class EmeraldOreEventHandler extends BlockBreakEventHandler {
    public EmeraldOreEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE});
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
    }
}
