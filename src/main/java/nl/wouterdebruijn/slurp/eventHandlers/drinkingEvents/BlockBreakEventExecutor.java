package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents;

import nl.wouterdebruijn.slurp.eventHandlers.ChanceExecutor;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;

abstract public class BlockBreakEventExecutor extends ChanceExecutor<BlockBreakEvent> {

    protected final Material[] materials;

    public BlockBreakEventExecutor(int chance, Material[] materials) {
        super(chance);
        this.materials = materials;
    }

    @Override
    protected boolean beforeExecution(BlockBreakEvent event) {
        return Arrays.asList(materials).contains(event.getBlock().getType());
    }
}
