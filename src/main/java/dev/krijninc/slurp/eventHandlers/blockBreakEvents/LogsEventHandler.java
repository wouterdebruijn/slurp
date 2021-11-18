package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class LogsEventHandler extends BlockBreakEventHandler {
    public LogsEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.ACACIA_LOG, Material.BIRCH_LOG, Material.OAK_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.SPRUCE_LOG});
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        event.getPlayer().sendMessage("HI! Logs " + amount);
    }
}
