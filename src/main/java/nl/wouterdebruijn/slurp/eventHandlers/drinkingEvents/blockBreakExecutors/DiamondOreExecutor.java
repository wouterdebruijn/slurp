package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockBreakExecutors;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.BlockBreakEventExecutor;
import nl.wouterdebruijn.slurp.exceptions.APIPostException;
import nl.wouterdebruijn.slurp.repository.SlurpEntryRepository;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class DiamondOreExecutor extends BlockBreakEventExecutor {
    public DiamondOreExecutor() {
        super(generateChange(100), new Material[]{Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE});
    }

    @Override
    protected void onExecution(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SlurpEntry entry = playerDrinkEvent(player.getUniqueId(), 1, 0, true);
        broadcastPlayerDrinking(player.getName(), entry.shots, entry.sips, entry.giveable, "diamonds");
    }
}
