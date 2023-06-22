package nl.wouterdebruijn.slurp.helper.game.events;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.manager.DrinkingBuddyManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

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

        // Only run if we have more than one player
        if (players.size() < amount)
            return null;

        int tries = 0;

        while (chosenPlayers.size() < amount && players.size() > 0) {
            // If we tried 100 times, stop
            if (tries++ > 100) {
                Slurp.logger.warning("Could not find enough players for drinking buddy event");
                return null;
            }

//        Dump players to log
            for (Player player : players) {
                Slurp.logger.info(player.getName());
            }

            Slurp.logger.info("players: " + players.size());

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
        drinkingBuddyTask = Bukkit.getScheduler().runTaskTimer(Slurp.plugin, () -> {
            ArrayList<SlurpPlayer> players = getRandomPlayersInSession(session, 2);

            if (players == null) {
                disable();
                return;
            }

            DrinkingBuddyManager.setDrinkingBuddies(session, players);

            // Call update on all players, reloading their scoreboards.
            for (SlurpPlayer player : SlurpPlayerManager.dump()) {
                player.update();
                Slurp.logger.info("Updated scoreboard for " + player.getUsername());
            }

            String names = String.join(", ", players.stream().map(SlurpPlayer::getUsername).toArray(String[]::new));

            // Display names of all drinking buddies in broadcast
            Slurp.plugin.getServer().broadcast(TextBuilder.info("Drinking buddies: " + names));

        }, 0, 20 * 60);
    }

    public void disable() {
        drinkingBuddyTask.cancel();
    }
}
