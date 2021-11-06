package dev.krijninc.slurp.eventHandlers;

import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakEventHandler {
    protected double amount;
    protected double chance;

    public BlockBreakEventHandler(double amount, double chance) {
        this.amount = amount;
        this.chance = chance;
    }

    public void execute(BlockBreakEvent event) {
        if (Math.random() <= chance) {
            handleEvent(event);
        }
    }

    protected void handleEvent(BlockBreakEvent event) {
    }
}