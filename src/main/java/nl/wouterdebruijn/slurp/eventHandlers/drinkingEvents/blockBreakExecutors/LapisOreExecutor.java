package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockBreakExecutors;

import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.BlockBreakEventExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class LapisOreExecutor extends BlockBreakEventExecutor {
    public LapisOreExecutor() {
        super(generateChange(0.17), new Material[]{Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE});
    }

    @Override
    protected void onExecution(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SlurpEntry entry = playerDrinkEvent(player.getUniqueId(), 0, 4, random.nextBoolean());
        broadcastPlayerDrinking(player.getName(), entry.shots, entry.sips, entry.giveable, "lapis");

    }
}
