package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class CopperOreEventHandler extends BlockBreakEventHandler {
    protected final Material[] materials = new Material[]{Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE};

    public CopperOreEventHandler(double amount, double chance) {
        super(amount, chance);
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        event.getPlayer().sendMessage("HI! Copper " + amount);
    }
}
