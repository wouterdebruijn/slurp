package dev.krijninc.slurp;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            DashboardServerConnector.post("/player", String.format("{\"player\": \"%s\"}", event.getPlayer().getUniqueId()));
        } catch (Exception e) {
            e.printStackTrace();
            Slurp.getFancyLogger().severe("Could not register new player on dashboard backend!");
        }
    }
}
