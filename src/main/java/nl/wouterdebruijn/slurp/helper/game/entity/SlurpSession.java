package nl.wouterdebruijn.slurp.helper.game.entity;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.endpoint.PocketBase;
import nl.wouterdebruijn.slurp.exceptions.ApiException;
import nl.wouterdebruijn.slurp.exceptions.ApiResponseException;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;

public class SlurpSession {
    private static final String API_URL = SlurpConfig.apiUrl();
    private final String shortcode;
    private final String uuid;
    private final boolean active;

    public SlurpSession(SlurpSession session) {
        this.shortcode = session.getShortcode();
        this.uuid = session.getUuid();
        this.active = session.isActive();
    }

    public SlurpSession(String shortcode, String uuid, boolean active) {
        this.shortcode = shortcode;
        this.uuid = uuid;
        this.active = active;
    }

    public static SlurpSession create() throws ApiException, ApiResponseException {
        return PocketBase.createSession();
    }

    public String getUuid() {
        return uuid;
    }

    public String getShortcode() {
        return shortcode;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Get all players in this session that are online in the Minecraft server
     *
     * @return ArrayList of SlurpPlayers
     */
    public ArrayList<SlurpPlayer> getPlayers() {
        ArrayList<SlurpPlayer> players = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player, this);

            if (slurpPlayer != null)
                players.add(slurpPlayer);
        }

        return players;
    }

    public ArrayList<SlurpPlayer> getRandomPlayersInSession(int amount) {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        // Filter players by SlurpPlayer existence
        players.removeIf(player -> SlurpPlayerManager.getPlayer(player, this) == null);

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

            SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player, this);

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
}
