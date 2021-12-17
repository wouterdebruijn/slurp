package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.eventHandlers.SlurpEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakEventHandler extends SlurpEventHandler<BlockBreakEvent> {
    @Override
    @EventHandler
    protected void onEvent(BlockBreakEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(Slurp.getPlugin(), () -> {
            // Run game logic with API call(s)
        });
    }
}
