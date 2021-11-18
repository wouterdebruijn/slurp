package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class StoneEventHandler extends BlockBreakEventHandler {
    public StoneEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.STONE, Material.DEEPSLATE});
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
    }
}
