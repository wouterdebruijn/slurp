package nl.wouterdebruijn.slurp.helper.game.events;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.RandomGenerator;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDiesEvent extends GameEvent implements Listener {
    public PlayerDiesEvent(FileConfiguration config) {
        super(config, "player-death");
    }

    @Override
    public GameEvent register(Slurp plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        return this;
    }

    @EventHandler
    public void onEvent(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNull(player, slurpPlayer)) {
            return;
        }

        // Check if the player has a killer do not trigger the event
        if (player.getKiller() != null) {
            return;
        }

        // Check if the player has a chance to trigger the event
        if (!this.chanceTrigger())
            return;

        // Trigger the event
        this.triggerFor(slurpPlayer);
    }
}
