package nl.wouterdebruijn.slurp.eventHandlers.utilityEvents;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.controller.LogController;
import nl.wouterdebruijn.slurp.controller.SidebarController;
import nl.wouterdebruijn.slurp.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.eventHandlers.SlurpEventHandler;
import nl.wouterdebruijn.slurp.exceptions.APIPostException;
import nl.wouterdebruijn.slurp.repository.SlurpPlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventHandler extends SlurpEventHandler<PlayerJoinEvent> {

    @Override
    @EventHandler
    protected void onEvent(PlayerJoinEvent event) {
        SlurpPlayer player = SlurpPlayerRepository.get(event.getPlayer().getUniqueId());

        if (player == null) {
            player = new SlurpPlayer(event.getPlayer().getUniqueId());

            SlurpPlayer finalPlayer = player;
            Bukkit.getScheduler().runTaskAsynchronously(Slurp.getPlugin(), () -> {
                try {
                    SlurpPlayer remotePlayer = SlurpPlayerRepository.register(finalPlayer);
                    if (remotePlayer == null) {
                        LogController.error("Could not get player existing from dashboard.");
                        throw new APIPostException();
                    }
                    SlurpPlayerRepository.put(remotePlayer);
                } catch (APIPostException e) {
                    e.printStackTrace();
                }
            });
            SidebarController.createSidebar(finalPlayer);
        }
    }
}
