package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class QuartzOreEventHandler extends BlockBreakEventHandler {
    public QuartzOreEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.NETHER_QUARTZ_ORE});
    }

    @Override
    protected void sendMessage(Player trigger, DrunkEntry entry) {

    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
    }
}
