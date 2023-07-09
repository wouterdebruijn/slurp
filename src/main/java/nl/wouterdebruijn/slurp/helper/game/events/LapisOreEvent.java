package nl.wouterdebruijn.slurp.helper.game.events;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class DiamondOreEvent extends GameEvent implements Listener {
    private static final ArrayList<Material> materials = new ArrayList<>(Arrays.asList(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE));

    public DiamondOreEvent(FileConfiguration config) {
        super(config, "diamond-ore");
    }

    @Override
    public GameEvent register(Slurp plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        return this;
    }

    @EventHandler
    public void onEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNull(player, slurpPlayer)) {
            return;
        }

        // Check if the mined block is a stone
        if (!materials.contains(event.getBlock().getType()))
            return;

        // Check if the player has a chance to trigger the event
        if (!this.chanceTrigger())
            return;

        // Trigger the event
        this.triggerFor(slurpPlayer);
    }
}
