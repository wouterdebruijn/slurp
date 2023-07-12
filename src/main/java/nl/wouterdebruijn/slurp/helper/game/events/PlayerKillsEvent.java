package nl.wouterdebruijn.slurp.helper.game.events;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKillsEvent extends GameEvent implements Listener {
    public PlayerKillsEvent(FileConfiguration config) {
        super(config, "player-kill");
    }

    @Override
    public GameEvent register(Slurp plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        return this;
    }

    @EventHandler
    public void onEvent(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        // Only trigger the event if the player has a killer
        if (player.getKiller() == null) {
            return;
        }

        // Check if the player has a chance to trigger the event
        if (!this.chanceTrigger())
            return;

        Player killer = player.getKiller();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(killer);

        if (SlurpPlayerManager.checkNullSilent(player, slurpPlayer)) {
            return;
        }

        // Trigger the event
        this.triggerFor(slurpPlayer);
    }
}
