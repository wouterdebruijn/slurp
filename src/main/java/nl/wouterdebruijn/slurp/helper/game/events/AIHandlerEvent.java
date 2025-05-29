package nl.wouterdebruijn.slurp.helper.game.events;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.advancement.Advancement;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.scheduler.BukkitTask;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.google.gson.JsonObject;

import io.papermc.paper.event.player.AsyncChatEvent;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.endpoint.GoogleAI;
import nl.wouterdebruijn.slurp.helper.ConfigValue;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.entity.EventLog;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;

public class AIHandlerEvent implements Listener {

    private final HashMap<String, EventLog> eventLogs = new HashMap<>();
    private final SlurpSession session;

    private BukkitTask task = null;

    public AIHandlerEvent(SlurpSession session) {
        this.session = session;
        Slurp.plugin.getServer().getPluginManager().registerEvents(this, Slurp.plugin);
    }

    /**
     * Schedules the AI handler to process events at a fixed interval.
     * The interval is defined in the SlurpConfig under
     * ConfigValue.GENERATION_INTERVAL.
     */
    public void schedule() {
        if (task != null) {
            Slurp.logger.warning("AIHandlerEvent task is already scheduled. Cancelling the previous task.");
            task.cancel();
            task = null;
        }

        task = Slurp.plugin.getServer().getScheduler().runTaskTimerAsynchronously(Slurp.plugin, () -> {
            if (eventLogs.isEmpty()) {
                return;
            }

            ArrayList<EventLog> logs = new ArrayList<>(eventLogs.values());
            eventLogs.clear();

            Slurp.logger.info("Processing " + logs.size() + " events for session: " + session.getId());

            // Process the logs with the AI handler
            GoogleAI.generateActions(logs, session);

            Slurp.logger.info("Processed " + logs.size() + " events for session: " + session.getId());
        }, 0, 20 * SlurpConfig.getValue(ConfigValue.GENERATION_INTERVAL));
    }

    /**
     * Cancels the scheduled AI handler task if it is running.
     * This method should be called when the session ends or when the AI handler
     * is no longer needed.
     */
    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

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

        Advancement advancement = event.getAdvancement();
        if (!(advancement.getKey().getKey().equals("root") && advancement.getKey().getNamespace().equals("minecraft")
                && advancement.getKey().getKey().startsWith("story"))) {
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
        Player player = null;
        Player damager = null;

        if (event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();
        }

        if (event.getDamageSource() != null && event.getDamageSource().getDirectEntity() instanceof Player) {
            damager = (Player) event.getDamageSource().getDirectEntity();
        }

        if (player == null && damager == null) {
            return; // No player involved in the damage event
        }

        String eventName = "EntityDamageEvent";

        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player != null ? player : damager);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();

        if (player != null) {
            data.addProperty("Player", player.getName());
        } else {
            data.addProperty("Entity", event.getEntity().getType().name());
        }

        data.addProperty("Damage", event.getFinalDamage());
        data.addProperty("Cause", event.getCause().name());
        if (damager != null) {
            data.addProperty("Damager", damager.getName());
        }

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        String eventName = "PlayerEatItemEvent";

        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());
        data.addProperty("Item", event.getItem().getType().name());

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void onEntityTameEvent(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player)) {
            return; // Only log events for players
        }

        String eventName = "PlayerTameEntityEvent";

        Player player = (Player) event.getOwner();
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
    public void onClickEvent(PlayerInteractEvent event) {
        String eventName = "PlayerInteractEvent";

        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return; // Only log right-click actions
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());
        data.addProperty("Action", event.getAction().name());

        if (event.getItem() != null) {
            data.addProperty("Item", event.getItem().getType().name());
        }

        if (event.getClickedBlock() != null) {
            data.addProperty("Block", event.getClickedBlock().getType().name());
        }

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void onPlayerSleepEvent(PlayerBedLeaveEvent event) {
        String eventName = "PlayerSleepEvent";

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
    public void onPlayerItemBreakEvent(PlayerItemBreakEvent event) {
        String eventName = "PlayerItemBreakEvent";

        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());
        data.addProperty("Item", event.getBrokenItem().getType().name());

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void onEntityBreedEvent(EntityBreedEvent event) {
        if (!(event.getBreeder() instanceof Player)) {
            return; // Only log events for players
        }

        String eventName = "PlayerBreedEvent";

        Player player = (Player) event.getBreeder();
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
    public void onPlayerThrowEggEvent(PlayerEggThrowEvent event) {
        String eventName = "PlayerThrowEggEvent";

        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());
        data.addProperty("Item", event.getEgg().getType().name());

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

    @EventHandler
    public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return; // Only log events for players
        }

        String eventName = "PlayerProjectileLaunchEvent";

        Player player = (Player) event.getEntity().getShooter();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(slurpPlayer)) {
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Player", player.getName());
        data.addProperty("Projectile", event.getEntity().getType().name());

        EventLog eventLog = new EventLog(eventName, data);
        logEvent(eventLog);

        Slurp.logger.info("Event logged: " + eventName + " - " + data);
    }

}
