package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class DiamondOreEventHandler extends BlockBreakEventHandler {
    protected final Material[] materials = new Material[]{Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE};

    public DiamondOreEventHandler(double amount, double chance) {
        super(amount, chance);
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        event.getPlayer().sendMessage("HI! Diamond " + amount);
    }
}
