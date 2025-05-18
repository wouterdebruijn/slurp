package nl.wouterdebruijn.slurp.endpoint;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.google.gson.Gson;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.exceptions.ApiResponseException;
import nl.wouterdebruijn.slurp.exceptions.ApiUrlException;
import nl.wouterdebruijn.slurp.exceptions.CreateSessionException;
import nl.wouterdebruijn.slurp.exceptions.MissingSessionException;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.api.ResponseEntry;
import nl.wouterdebruijn.slurp.helper.game.api.ResponsePlayer;
import nl.wouterdebruijn.slurp.helper.game.api.ResponseSession;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpSessionManager;

public class PocketBase {
    private static final String API_URL = SlurpConfig.apiUrl();

    private static PocketBase instance;
    private static Gson gson;

    private String token;

    public static PocketBase getInstance() {
        if (instance == null) {
            instance = new PocketBase();
        }
        return instance;
    }

    public static void initialize() {
        instance = new PocketBase();
    }

    public static CompletableFuture<SlurpEntry> createEntry(SlurpEntryBuilder entry) {
        try {
            Gson gson = new Gson();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/api/collections/entry/records"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(entry)))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + instance.token)
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiResponseException(request, response);
            }

            ResponseEntry responseEntry = gson.fromJson(response.body(), ResponseEntry.class);
            return CompletableFuture.completedFuture(responseEntry.toSlurpEntry());
        } catch (Exception e) {
            Slurp.logger.log(Level.SEVERE, "Error while creating entry", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public static CompletableFuture<SlurpPlayer> createPlayer(Player player, String sessionShort)
            throws CreateSessionException, ApiUrlException, ApiResponseException, MissingSessionException {
        HttpRequest request = null;

        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/api/collections/player/records"))
                    .POST(HttpRequest.BodyPublishers.ofString(String
                            .format("{\"session\": \"%s\", \"username\": \"%s\"}", sessionShort, player.getName())))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + instance.token)
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiResponseException(request, response);
            }

            ResponsePlayer responsePlayer = gson.fromJson(response.body(), ResponsePlayer.class);
            Slurp.logger.info(gson.toJson(responsePlayer));

            SlurpPlayer slurpPlayer = responsePlayer.toSlurpPlayer();
            Slurp.logger.log(Level.INFO, String.format("Created player %s", slurpPlayer.getPlayer().getName()));

            return CompletableFuture.completedFuture(slurpPlayer);
        } catch (URISyntaxException e) {
            throw new ApiUrlException();
        } catch (IOException | InterruptedException e) {
            throw new CreateSessionException(request);
        }
    }

    public static SlurpSession createSession()
            throws ApiResponseException, ApiUrlException, CreateSessionException {
        HttpRequest request = null;

        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/session"))
                    .POST(HttpRequest.BodyPublishers.ofString("{}"))
                    .header("Content-Type", "application/json")
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiResponseException(request, response);
            }

            ResponseSession responseSession = gson.fromJson(response.body(), ResponseSession.class);
            Slurp.logger.info(gson.toJson(responseSession));

            SlurpSession session = responseSession.toSlurpSession();
            Slurp.logger.log(Level.INFO,
                    String.format("Created session %s, (%s)", session.getShortcode(), session.getUuid()));

            SlurpSessionManager.addSession(session);

            return session;
        } catch (URISyntaxException e) {
            throw new ApiUrlException();
        } catch (IOException | InterruptedException e) {
            throw new CreateSessionException(request);
        }
    }

}
