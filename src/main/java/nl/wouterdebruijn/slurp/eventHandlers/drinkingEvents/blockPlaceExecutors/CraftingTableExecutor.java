package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockPlaceExecutors;

import nl.wouterdebruijn.slurp.controller.ConfigController;
import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.BlockPlaceEventExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

public class CraftingTableExecutor extends BlockPlaceEventExecutor {
    public CraftingTableExecutor() {
        super(generateChange(ConfigController.getDouble("drinking-events.block-place-events.craftingtable-chance")), new Material[]{Material.CRAFTING_TABLE});
    }

    @Override
    protected void onExecution(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        SlurpEntry entry = playerDrinkEvent(player.getUniqueId(), 0, 5, false);
        broadcastPlayerDrinking(player.getName(), entry.shots, entry.sips, entry.giveable, "craftingtable");
    }
}
