package dev.krijninc.slurp.eventHandlers;

import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Arrays;

public abstract class BlockPlaceEventHandler extends EventHandler<BlockPlaceEvent> {
    protected final Material[] materials;

    public BlockPlaceEventHandler(double amount, double chance, Material[] materials) {
        super(amount, chance);
        this.materials = materials;
    }

    public void execute(BlockPlaceEvent event, Material material) {
        if (matchesMaterial(material) && Math.random() <= chance) {
            handleEvent(event);
        }
    }

    protected boolean matchesMaterial(Material material) {
        return Arrays.asList(this.materials).contains(material);
    }
}