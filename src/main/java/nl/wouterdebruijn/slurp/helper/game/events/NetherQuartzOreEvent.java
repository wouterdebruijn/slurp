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
import java.util.List;

public class NetherQuartzOreEvent extends GameEvent implements Listener {
    private static final ArrayList<Material> materials = new ArrayList<>(List.of(Material.NETHER_QUARTZ_ORE));

    public NetherQuartzOreEvent(FileConfiguration config) {
        super(config, "quartz-ore");
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

        if (SlurpPlayerManager.checkNullSilent(player, slurpPlayer)) {
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
