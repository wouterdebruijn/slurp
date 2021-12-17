package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockBreakExecutors;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.BlockBreakEventExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class IronOreExecutor extends BlockBreakEventExecutor {
    public IronOreExecutor() {
        super(100, new Material[]{Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE});
    }

    @Override
    protected void onExecution(BlockBreakEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("Block break event Iron!");

        Bukkit.getScheduler().runTaskAsynchronously(Slurp.getPlugin(), () -> {
            // API calls etc.
        });
    }
}
