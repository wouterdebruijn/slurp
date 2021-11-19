package dev.krijninc.slurp.eventHandlers;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;

public abstract class BlockBreakEventHandler extends EventHandler<BlockBreakEvent> {
    protected final Material[] materials;

    public BlockBreakEventHandler(double amount, double chance, Material[] materials) {
        super(amount, chance);
        this.materials = materials;
    }

    public void execute(BlockBreakEvent event, Material material) {
        if (matchesMaterial(material) && Math.random() <= chance) {
            handleEvent(event);
        }
    }

    protected boolean matchesMaterial(Material material) {
        return Arrays.asList(this.materials).contains(material);
    }
}