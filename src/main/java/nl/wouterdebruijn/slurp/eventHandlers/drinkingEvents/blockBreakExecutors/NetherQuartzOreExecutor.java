package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockBreakExecutors;

import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.BlockBreakEventExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class NetherQuartzOreExecutor extends BlockBreakEventExecutor {
    public NetherQuartzOreExecutor() {
        super(generateChange(0.08), new Material[]{Material.NETHER_QUARTZ_ORE});
    }

    @Override
    protected void onExecution(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SlurpEntry entry = playerDrinkEvent(player.getUniqueId(), 0, 2, random.nextBoolean());
        broadcastPlayerDrinking(player.getName(), entry.shots, entry.sips, entry.giveable, "quartz");
    }
}
