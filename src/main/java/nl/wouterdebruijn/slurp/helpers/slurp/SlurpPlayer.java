package nl.wouterdebruijn.slurp.helpers.slurp;

import com.google.gson.Gson;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helpers.SlurpConfig;
import nl.wouterdebruijn.slurp.helpers.slurp.exceptions.ApiResponseException;
import nl.wouterdebruijn.slurp.helpers.slurp.exceptions.ApiUrlException;
import nl.wouterdebruijn.slurp.helpers.slurp.exceptions.CreateSessionException;
import nl.wouterdebruijn.slurp.helpers.slurp.exceptions.MissingSessionException;
import nl.wouterdebruijn.slurp.helpers.slurp.gson.ResponsePlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;

public class SlurpPlayer {
    private String uuid;
    private SlurpSession session;
    private String username;

    private static final String API_URL = SlurpConfig.ApiUrl();

    public SlurpPlayer(String uuid, SlurpSession session, String username) {
        this.uuid = uuid;
        this.session = session;
        this.username = username;
    }

    public SlurpPlayer(SlurpPlayerGson slurpPlayerGson) {
        this.uuid = slurpPlayerGson.getUuid();
        this.session = slurpPlayerGson.getSession();
        this.username = slurpPlayerGson.getUsername();
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
            SlurpPlayer slurpPlayer = responsePlayer.toSlurpPlayer();
            Slurp.logger.log(Level.INFO, String.format("Created player %s", slurpPlayer.getUsername()));
            SlurpPlayerManager.addPlayer(player, slurpPlayer);
            return slurpPlayer;
        } catch (URISyntaxException e) {
            throw new ApiUrlException();
        } catch (IOException | InterruptedException e) {
            throw new CreateSessionException(request);
        }
    }

    public Player getMinecraftPlayer() {
        return Slurp.plugin.getServer().getPlayer(username);
    }

    public String getUuid() {
        return uuid;
    }

    public SlurpSession getSession() {
        return session;
    }

    public String getUsername() {
        return username;
    }


}
