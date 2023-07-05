package nl.wouterdebruijn.slurp.helper.game.entity;

import com.google.gson.Gson;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.exceptions.ApiResponseException;
import nl.wouterdebruijn.slurp.exceptions.ApiUrlException;
import nl.wouterdebruijn.slurp.exceptions.CreateSessionException;
import nl.wouterdebruijn.slurp.exceptions.MissingSessionException;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.api.ResponsePlayer;
import nl.wouterdebruijn.slurp.helper.game.filestorage.SlurpPlayerFileAdapter;
import nl.wouterdebruijn.slurp.helper.game.manager.ScoreboardManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.infra.Subject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;

public class SlurpPlayer extends Subject {
    private static final String API_URL = SlurpConfig.apiUrl();
    private final String uuid;
    private final SlurpSession session;
    private final String username;

    private final Consumable taken;
    private final Consumable giveable;
    private final Consumable remaining;

    public SlurpPlayer(String uuid, SlurpSession session, String username) {
        this.uuid = uuid;
        this.session = session;
        this.username = username;
        this.taken = new Consumable();
        this.giveable = new Consumable();
        this.remaining = new Consumable();
    }

    public SlurpPlayer(SlurpPlayerFileAdapter slurpPlayerFileAdapter) {
        this.uuid = slurpPlayerFileAdapter.getUuid();
        this.session = slurpPlayerFileAdapter.getSession();
        this.username = slurpPlayerFileAdapter.getUsername();
        this.taken = new Consumable();
        this.giveable = new Consumable();
        this.remaining = new Consumable();
    }

    public static SlurpPlayer create(Player player, String sessionShort) throws ApiUrlException, CreateSessionException, MissingSessionException, ApiResponseException {
        Gson gson = new Gson();
        HttpRequest request = null;

        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/player"))
                    .POST(HttpRequest.BodyPublishers.ofString(String.format("{\"session\": \"%s\", \"username\": \"%s\"}", sessionShort, player.getName())))
                    .header("Content-Type", "application/json")
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiResponseException(request, response);
            }

            ResponsePlayer responsePlayer = gson.fromJson(response.body(), ResponsePlayer.class);
            Slurp.logger.info(gson.toJson(responsePlayer));

            SlurpPlayer slurpPlayer = responsePlayer.toSlurpPlayer();
            Slurp.logger.log(Level.INFO, String.format("Created player %s", slurpPlayer.getUsername()));
            SlurpPlayerManager.addPlayer(player, slurpPlayer);

            ScoreboardManager.playerScoreboard(slurpPlayer);

            return slurpPlayer;
        } catch (URISyntaxException e) {
            throw new ApiUrlException();
        } catch (IOException | InterruptedException e) {
            throw new CreateSessionException(request);
        }
    }

    public String getUuid() {
        return uuid;
    }

    public SlurpSession getSession() {
        return session;
    }

    /**
     * Get the username of the player
     * @return The username
     * @deprecated Use {@link #getPlayer()} instead
     */
    @Deprecated
    public String getUsername() {
        return username;
    }

    /**
     * Get the bukkit player matching this slurp player
     * @return The bukkit player, or null if not found
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(this.username);
    }

    public Consumable getTaken() {
        return taken;
    }

    public void setTaken(Consumable taken) {
        this.taken.set(taken);
        this.notifyObservers();
    }

    public Consumable getGiveable() {
        return giveable;
    }

    public void setGiveable(Consumable giveable) {
        this.giveable.set(giveable);
        this.notifyObservers();
    }

    public Consumable getRemaining() {
        return remaining;
    }

    public void setRemaining(Consumable remaining) {
        this.remaining.set(remaining);
        this.notifyObservers();
    }

    public void update() {
        this.notifyObservers();
    }
}
