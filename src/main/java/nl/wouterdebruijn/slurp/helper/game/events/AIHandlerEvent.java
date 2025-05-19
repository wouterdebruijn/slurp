package nl.wouterdebruijn.slurp.helper.game.events;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.google.gson.JsonObject;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.game.entity.EventLog;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;

public class AIHandlerEvent implements Listener {

    HashMap<String, EventLog> eventLogs = new HashMap<>();

    public ArrayList<EventLog> getEventLogs() {
        return new ArrayList<EventLog>(eventLogs.values());
    }

    public void clearEventLogs() {
        eventLogs.clear();
    }

    private void logEvent(EventLog eventLog) {
        if (eventLogs.containsKey(eventLog.getDataString())) {
            eventLogs.get(eventLog.getDataString()).incrementCount();
            return;
        }

        eventLogs.put(eventLog.getDataString(), eventLog);
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        String eventName = event.getEventName();

        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        Player killer = event.getEntity().getKiller();

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());

        if (killer != null) {
            data.addProperty("Killer", killer.getName());
        }
        data.addProperty("Cause", event.getEntity().getLastDamageCause().getCause().name());

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        String eventName = event.getEventName();

        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());
        data.addProperty("Block", event.getBlock().getType().name());
        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        String eventName = event.getEventName();

        LivingEntity entity = event.getEntity();

        Player player = entity.getKiller();

        if (player == null) {
            return;
        }

        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());
        data.addProperty("Entity", event.getEntity().getType().name());

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

}
