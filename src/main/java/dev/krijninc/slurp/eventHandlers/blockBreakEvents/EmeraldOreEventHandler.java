package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class EmeraldOreEventHandler extends BlockBreakEventHandler {
    public EmeraldOreEventHandler(double amount, double chance) {
        super(amount, chance);
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        event.getPlayer().sendMessage("HI! Emerald " + amount);
    }
}
