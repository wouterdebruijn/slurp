package nl.wouterdebruijn.slurp.helper.game.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.handlers.TitleCountdownHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.DrinkingBuddyManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class DrinkingBuddyEvent {
    SlurpSession session;
    BukkitTask drinkingBuddyTask;

    public DrinkingBuddyEvent(SlurpSession session) {
        this.session = session;
    }

    public String getSessionId() {
        return session.getUuid();
    }

    private ArrayList<SlurpPlayer> getRandomPlayersInSession(SlurpSession session, int amount) {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        ArrayList<SlurpPlayer> chosenPlayers = new ArrayList<>();

        // Only run if we have one more player than the amount of drinking buddies
        if (players.size() < amount + 1)
            return null;

        int tries = 0;

        while (chosenPlayers.size() < amount && players.size() > 0) {
            // If we tried 100 times, stop
            if (tries++ > 100) {
                Slurp.logger.warning("Could not find enough players for drinking buddy event");
                return null;
            }

            // Get random player
            int randomIndex = ThreadLocalRandom.current().nextInt(players.size());
            Player player = players.get(randomIndex);
            players.remove(randomIndex);

            SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player, session);

            if (slurpPlayer == null) {
                continue;
            }

            // Add player to chosen players
            chosenPlayers.add(slurpPlayer);
        }

        if (chosenPlayers.size() < amount) {
            return null;
        }

        return chosenPlayers;
    }

    public void enable() {
        drinkingBuddyTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Slurp.plugin, () -> {
            ArrayList<SlurpPlayer> players = getRandomPlayersInSession(session, 2);

            if (players == null) {
                DrinkingBuddyManager.disableDrinkingBuddyEvent(session);
                return;
            }

            // TODO: Look into Future and CompletableFuture for async stuff
            ArrayList<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

            // Run multiple async tasks and wait for them to finish
            CompletableFuture[] futures = new CompletableFuture[onlinePlayers.size()];

            for (Player player : onlinePlayers) {
                    CompletableFuture<Void> future = TitleCountdownHandler.countdown(player, 5).thenCompose((Void) -> {
                        boolean isDrinkingBuddy = players.contains(SlurpPlayerManager.getPlayer(player, session));

                        if (isDrinkingBuddy) {
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                            player.showTitle(Title.title(
                                    Component.text("Drinking Buddy", NamedTextColor.GREEN),
                                    Component.text("You are a drinking buddy!", NamedTextColor.GREEN)
                            ));
                        } else {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                            player.showTitle(Title.title(
                                    Component.text("Not a Drinking Buddy", NamedTextColor.RED),
                                    Component.text("You are not a drinking buddy!", NamedTextColor.RED)
                            ));
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

        }, 20 * 5, 20 * 60);
        // 5 seconds delay, 60 seconds interval
    }

    public void disable() {
        drinkingBuddyTask.cancel();
    }
}
