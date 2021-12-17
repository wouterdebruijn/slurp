package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.blockBreakExecutors;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.api.SlurpAPI;
import nl.wouterdebruijn.slurp.controller.LogController;
import nl.wouterdebruijn.slurp.eventHandlers.ChanceExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;

public class IronOreExecutor extends ChanceExecutor<BlockBreakEvent> {

    protected final Material[] materials = {Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE};

    public IronOreExecutor(int chance) {
        super(chance);
    }

    @Override
    protected void onExecution(BlockBreakEvent event) {
        LogController.info(event.getBlock().getType().toString());
        Player player = event.getPlayer();
        player.sendMessage("Block break event Iron!");

        Bukkit.getScheduler().runTaskAsynchronously(Slurp.getPlugin(), () -> {
            try {
                LogController.info("before API Call");
                SlurpAPI.get("");
                LogController.info("After API Call");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected boolean beforeExecution(BlockBreakEvent event) {
        return Arrays.asList(materials).contains(event.getBlock().getType());
    }
}
