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

public class CoalOreExecutor extends BlockBreakEventExecutor {
    public CoalOreExecutor() {
        super(100, new Material[]{Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE});
    }

    @Override
    protected void onExecution(BlockBreakEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("Block break event Coal!");

        SlurpEntry entry = new SlurpEntry(player.getUniqueId(), 5, 1, false, false);
        SlurpEntryRepository.cache(entry, true);

        Bukkit.getScheduler().runTaskAsynchronously(Slurp.getPlugin(), () -> {
            try {
                SlurpEntryRepository.save(entry);
            } catch (APIPostException e) {
                e.printStackTrace();
            }
        });
    }
}
