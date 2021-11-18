package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class RedstoneOreEventHandler extends BlockBreakEventHandler {
    protected final Material[] materials = new Material[]{Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE};

    public RedstoneOreEventHandler(double amount, double chance) {
        super(amount, chance);
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        event.getPlayer().sendMessage("HI! Redstone " + amount);
    }
}
