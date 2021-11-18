package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class GoldOreEventHandler extends BlockBreakEventHandler {
    protected final Material[] materials = new Material[]{Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE};

    public GoldOreEventHandler(double amount, double chance) {
        super(amount, chance);
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        event.getPlayer().sendMessage("HI! GOLD " + amount);
    }
}
