package nl.wouterdebruijn.slurp.helper.game.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.handlers.TitleHandler;

/**
 * Manager for drinking buddy tasks
 */
public class DrinkingBuddyManager {
    /**
     * The list of drinking buddies for each session.
     */
    static final HashMap<String, ArrayList<SlurpPlayer>> drinkingBuddies = new HashMap<>();
    /**
     * List of active drinking buddy tasks.
     */
    static final ArrayList<DrinkingBuddyTask> drinkingBuddyTasks = new ArrayList<>();

    /**
     * Enable the drinking buddy event for a session.
     *
     * @param session The session to enable the event for.
     */
    public static void enableDrinkingBuddyTask(SlurpSession session) {
        // Check if event is already enabled
        for (DrinkingBuddyTask task : drinkingBuddyTasks) {
            if (task.getSessionId().equals(session.getId())) {
                return;
            }
        }

        DrinkingBuddyTask task = new DrinkingBuddyTask(session);
        task.enable();
        drinkingBuddyTasks.add(task);
    }

    /**
     * Disable the drinking buddy event for a session.
     *
     * @param session The session to disable the event for.
     */
    public static void disableDrinkingBuddyTask(SlurpSession session) {
        for (DrinkingBuddyTask event : drinkingBuddyTasks) {
            if (event.getSessionId().equals(session.getId())) {
                event.disable();
                drinkingBuddyTasks.remove(event);
                deleteDrinkingBuddies(session);
                return;
            }
        }
    }

    public static void restartDrinkingBuddyTask(SlurpSession session) {
        disableDrinkingBuddyTask(session);
        enableDrinkingBuddyTask(session);
    }

    private static void setDrinkingBuddies(SlurpSession session, ArrayList<SlurpPlayer> players) {
        drinkingBuddies.put(session.getId(), players);
    }

    private static void deleteDrinkingBuddies(SlurpSession session) {
        drinkingBuddies.remove(session.getId());
    }

    public static ArrayList<SlurpPlayer> getDrinkingBuddies(SlurpSession session) {
        ArrayList<SlurpPlayer> buddies = drinkingBuddies.get(session.getId());
        if (buddies == null) {
            return new ArrayList<>();
        }
        return buddies;
    }

    /**
     * Return the list of drinking buddies of the given player, excluding the player
     * itself.
     */
    public static ArrayList<SlurpPlayer> getBuddiesOfPlayer(SlurpPlayer player) {
        // Get drinking buddies and create copy of the list.
        ArrayList<SlurpPlayer> buddies = new ArrayList<>(getDrinkingBuddies(player.getSession()));

        for (SlurpPlayer buddy : buddies) {
            if (buddy.getId().equals(player.getId())) {
                // Remove the player itself from the list
                buddies.remove(buddy);
                return buddies;
            }
        }
        return null;
    }

    /**
     * The drinking buddy task which chooses different drinking buddies every couple
     * of minutes.
     * Adapting the bukkit task linking it to a SlurpSession.
     */
    private static class DrinkingBuddyTask {
        SlurpSession session;
        BukkitTask bukkitTask;

        public DrinkingBuddyTask(SlurpSession session) {
            this.session = session;
        }

        public String getSessionId() {
            return session.getId();
        }

        public void enable() {
            bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Slurp.plugin, () -> {
                ArrayList<SlurpPlayer> players = session.getRandomPlayersInSession(2);

                if (players == null) {
                    DrinkingBuddyManager.disableDrinkingBuddyTask(session);
                    return;
                }

                ArrayList<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

                // Run multiple async tasks and wait for them to finish
                CompletableFuture<?>[] futures = new CompletableFuture[onlinePlayers.size()];

                for (Player player : onlinePlayers) {
                    CompletableFuture<Void> future = TitleHandler.countdown(player, 5).thenCompose((Void) -> {
                        boolean isDrinkingBuddy = players.contains(SlurpPlayerManager.getPlayer(player, session));

                        if (isDrinkingBuddy) {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1, 1);
                            player.showTitle(Title.title(
                                    Component.text("Drinking Buddy", NamedTextColor.GREEN),
                                    Component.text("You are a drinking buddy!", NamedTextColor.GREEN)));
                        } else {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                            player.showTitle(Title.title(
                                    Component.text("Not a Drinking Buddy", NamedTextColor.GOLD),
                                    Component.text("You are not a drinking buddy!", NamedTextColor.GOLD)));
                        }
                        return CompletableFuture.completedFuture(null);
                    });

                    futures[onlinePlayers.indexOf(player)] = future;
                }

                // Wait for all futures to finish
                CompletableFuture.allOf(futures).join();

                DrinkingBuddyManager.setDrinkingBuddies(session, players);

                // Call update on all players, reloading their scoreboards.
                for (SlurpPlayer player : SlurpPlayerManager.dump()) {
                    player.update();
                }

                // Display broadcast for drinking buddies
                String names = String.join(", ", players.stream().map(SlurpPlayer::getUsername).toArray(String[]::new));
                // Display names of all drinking buddies in broadcast
                Slurp.plugin.getServer().broadcast(TextBuilder.info("Drinking buddies: " + names));

            }, 20 * 5, 20 * 60 * 15);
            // 5 seconds delay, 15 minute interval
        }

        public void disable() {
            bukkitTask.cancel();
        }
    }
}
