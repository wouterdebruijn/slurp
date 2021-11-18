package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class NetherGoldOreEventHandler extends BlockBreakEventHandler {
    public NetherGoldOreEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.NETHER_GOLD_ORE});
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        event.getPlayer().sendMessage("HI! NehterGold " + amount);
    }
}
