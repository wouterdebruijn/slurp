package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class DiamondOreEventHandler extends BlockBreakEventHandler {
    public DiamondOreEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE});
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        event.getPlayer().sendMessage("HI! Diamond " + amount);
    }
}
