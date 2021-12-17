package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockBreakExecutors;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.BlockBreakEventExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class CoalOreExecutor extends BlockBreakEventExecutor {
    public CoalOreExecutor() {
        super(100, new Material[]{Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE});
    }

    @Override
    protected void onExecution(BlockBreakEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("Block break event Coal!");

        Bukkit.getScheduler().runTaskAsynchronously(Slurp.getPlugin(), () -> {
            // API calls etc.
        });
    }
}
