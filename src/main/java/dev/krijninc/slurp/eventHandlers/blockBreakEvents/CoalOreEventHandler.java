package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class CoalOreEventHandler extends BlockBreakEventHandler {
    protected final Material[] materials = new Material[]{Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE};

    public CoalOreEventHandler(double amount, double chance) {
        super(amount, chance);
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        event.getPlayer().sendMessage("HI! Coal " + amount);
    }
}
