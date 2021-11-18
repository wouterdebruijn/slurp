package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class IronOreEventHandler extends BlockBreakEventHandler {
    protected final Material[] materials = new Material[]{Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE};

    public IronOreEventHandler(double amount, double chance) {
        super(amount, chance);
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        event.getPlayer().sendMessage("HI! Iron " + amount);
    }
}
