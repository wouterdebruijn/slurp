package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class LapisOreEventHandler extends BlockBreakEventHandler {
    protected final Material[] materials = new Material[]{Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE};

    public LapisOreEventHandler(double amount, double chance) {
        super(amount, chance);
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        event.getPlayer().sendMessage("HI! Lapis " + amount);
    }
}
