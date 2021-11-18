package dev.krijninc.slurp.eventHandlers;

import dev.krijninc.slurp.Slurp;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;

public class BlockBreakEventHandler {
    protected double amount;
    protected double chance;

    protected final Material[] materials;

    public BlockBreakEventHandler(double amount, double chance, Material[] materials) {
        this.amount = amount;
        this.chance = chance;
        this.materials = materials;
    }

    public void execute(BlockBreakEvent event, Material material) {
        if (matchesMaterial(material) && Math.random() <= chance) {
            handleEvent(event);
        }
    }

    protected void handleEvent(BlockBreakEvent event) {
    }

    private boolean matchesMaterial(Material material) {
        return Arrays.asList(this.materials).contains(material);
    }
}