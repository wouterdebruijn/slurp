package nl.wouterdebruijn.slurp.helper.game.events;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.google.gson.JsonObject;

import io.papermc.paper.event.player.AsyncChatEvent;
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
        data.addProperty("Entity", event.getEntity().getType().name());
        data.addProperty("Cause",
                event.getEntity().getLastDamageCause() != null
                        ? event.getEntity().getLastDamageCause().getCause().name()
                        : "Unknown");

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

    @EventHandler
    public void onPlayerJumpEvent(PlayerJumpEvent event) {
        String eventName = "PlayerJumpEvent";

        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        String eventName = "BlockPlaceEvent";

        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());
        data.addProperty("Block", event.getBlockPlaced().getType().name());
        if (event.getItemInHand() != null) {
            data.addProperty("Item", event.getItemInHand().getType().name());
        }
        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent event) {
        String eventName = "PlayerCraftEvent";

        Player player = (Player) event.getWhoClicked();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());
        data.addProperty("Item", event.getRecipe().getResult().getType().name());
        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void onAdvancementEvent(PlayerAdvancementDoneEvent event) {
        String eventName = "PlayerAdvancementEvent";

        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());
        data.addProperty("Advancement", event.getAdvancement().getKey().toString());

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void onPlayerRespawnEvent(org.bukkit.event.player.PlayerRespawnEvent event) {
        String eventName = "PlayerRespawnEvent";

        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void onPlayerTeleportEvent(org.bukkit.event.player.PlayerTeleportEvent event) {
        String eventName = "PlayerTeleportEvent";

        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());
        data.addProperty("From", event.getFrom().toString());
        data.addProperty("To", event.getTo().toString());

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncChatEvent event) {
        String eventName = "PlayerChatEvent";

        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());
        data.addProperty("Message", event.message().toString());

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void onPickupItemEvent(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        String eventName = "PlayerPickupItemEvent";

        Player player = (Player) event.getEntity();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());
        data.addProperty("Item", event.getItem().getItemStack().getType().name());
        data.addProperty("Count", event.getItem().getItemStack().getAmount());

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void onDropItemEvent(PlayerDropItemEvent event) {
        String eventName = "PlayerDropItemEvent";

        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());
        data.addProperty("Item", event.getItemDrop().getItemStack().getType().name());
        data.addProperty("Count", event.getItemDrop().getItemStack().getAmount());

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        String eventName = "PlayerDamageEvent";

        Player player = (Player) event.getEntity();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());
        data.addProperty("Damage", event.getFinalDamage());
        data.addProperty("Cause", event.getCause().name());

        Player damager = null;
        if (event.getDamageSource() != null && event.getDamageSource().getDirectEntity() instanceof Player) {
            damager = (Player) event.getDamageSource().getDirectEntity();
            data.addProperty("Damager", damager.getName());
        }

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }
}
