package nl.wouterdebruijn.slurp.listener;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.manager.DrinkingBuddyManager;
import nl.wouterdebruijn.slurp.helper.game.manager.ScoreboardManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpSessionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SlurpSessionSubscriptionListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SlurpPlayer player = SlurpPlayerManager.getPlayer(event.getPlayer());

        if (player == null) {
            return;
        }

        SlurpSession session = SlurpSessionManager.getSession(player.getSession().getUuid());

        if (session == null) {
            Slurp.logger.warning("Player " + player.getUuid() + " is in a session that does not exist in sessionmanager storage");
        }

        ScoreboardManager.playerScoreboard(player);
        SlurpSessionManager.subscribeToSession(session);
        DrinkingBuddyManager.enableDrinkingBuddyTask(session);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        SlurpPlayer player = SlurpPlayerManager.getPlayer(event.getPlayer());

        if (player == null) {
            return;
        }

        SlurpSession session = SlurpSessionManager.getSession(player.getSession().getUuid());

        if (session == null) {
            return;
        }

        // Check if there are any other players online in the session, if not, unsubscribe from the session
        for (Player onlinePlayer : Slurp.plugin.getServer().getOnlinePlayers()) {

            // Don't check the player that just left
            if (onlinePlayer.getUniqueId().equals(event.getPlayer().getUniqueId())) {
                continue;
            }

            SlurpPlayer onlineSlurpPlayer = SlurpPlayerManager.getPlayer(onlinePlayer);
            if (onlineSlurpPlayer.getSession().getUuid().equals(session.getUuid())) {
                return;
            }
        }

        SlurpSessionManager.unsubscribeFromSession(session);
        DrinkingBuddyManager.disableDrinkingBuddyTask(session);
    }
}
