package nl.wouterdebruijn.slurp.endpoint;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

    private String token;

    private PocketBase(String token) {
        this.token = token;
    }

    public static PocketBase getInstance() {
        return instance;
    }

    public static void initialize(String token) {
        instance = new PocketBase(token);
    }

    public static CompletableFuture<SlurpEntry> createEntry(SlurpEntryBuilder entry) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/api/collections/entries/records"))
                    .POST(HttpRequest.BodyPublishers.ofString(PocketBaseGson.getGson().toJson(entry)))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + instance.token)
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiResponseException(request, response);
            }

            ResponseEntry responseEntry = PocketBaseGson.getGson().fromJson(response.body(), ResponseEntry.class);
            return CompletableFuture.completedFuture(responseEntry.toSlurpEntry());
        } catch (Exception e) {
            Slurp.logger.log(Level.SEVERE, "Error while creating entry", e);
            Slurp.logger.info("Request: " + PocketBaseGson.getGson().toJson(entry));
            return CompletableFuture.failedFuture(e);
        }
    }

    public static CompletableFuture<SlurpPlayer> createPlayer(Player player, String sessionId)
            throws CreateSessionException, ApiUrlException, ApiResponseException, MissingSessionException {
        HttpRequest request = null;

        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/api/collections/players/records"))
                    .POST(HttpRequest.BodyPublishers.ofString(String
                            .format("{\"session\": \"%s\", \"username\": \"%s\"}", sessionId, player.getName())))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + instance.token)
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiResponseException(request, response);
            }

            ResponsePlayer responsePlayer = PocketBaseGson.getGson().fromJson(response.body(), ResponsePlayer.class);
            Slurp.logger.info(PocketBaseGson.getGson().toJson(responsePlayer));

            SlurpPlayer slurpPlayer = responsePlayer.toSlurpPlayer();
            Slurp.logger.log(Level.INFO, String.format("Created player %s", slurpPlayer.getPlayer().getName()));

            return CompletableFuture.completedFuture(slurpPlayer);
        } catch (URISyntaxException e) {
            throw new ApiUrlException();
        } catch (IOException | InterruptedException e) {
            throw new CreateSessionException(request);
        }
    }

    private static final String[] CODES = {
            "vodka", "whiskey", "rum", "gin", "tequila", "brandy", "cognac", "absinthe", "schnapps", "bourbon"
    };

    private static String generateShortcode() {
        StringBuilder shortcode = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            int randomIndex = (int) (Math.random() * CODES.length);
            shortcode.append(CODES[randomIndex]);
            if (i < 2) {
                shortcode.append("-");
            }
        }
        shortcode.append((int) (Math.random() * 1000)); // Append a random number to ensure uniqueness
        return shortcode.toString();
    }

    public static SlurpSession createSession()
            throws ApiResponseException, ApiUrlException, CreateSessionException {
        HttpRequest request = null;

        try {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("shortcode", generateShortcode());

            request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/api/collections/sessions/records"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                    .header("Content-Type", "application/json")
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiResponseException(request, response);
            }

            ResponseSession responseSession = PocketBaseGson.getGson().fromJson(response.body(), ResponseSession.class);
            Slurp.logger.info(PocketBaseGson.getGson().toJson(responseSession));

            SlurpSession session = responseSession.toSlurpSession();
            Slurp.logger.log(Level.INFO,
                    String.format("Created session %s, (%s)", session.getShortcode(), session.getId()));

            SlurpSessionManager.addSession(session);

            return session;
        } catch (URISyntaxException e) {
            throw new ApiUrlException();
        } catch (IOException | InterruptedException e) {
            throw new CreateSessionException(request);
        }
    }

    public static void registerSubscriptions(String clientId, ArrayList<String> subscriptions)
            throws ApiResponseException, ApiUrlException, CreateSessionException {
        HttpRequest request = null;

        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("clientId", clientId);

            JsonElement element = PocketBaseGson.getGson().toJsonTree(subscriptions);

            jsonObject.add("subscriptions", element);

            Slurp.logger.info("Registering SSE subscriptions: " + jsonObject.toString());

            request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/api/realtime"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + SlurpConfig.getToken())
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 && response.statusCode() != 204) {
                throw new ApiResponseException(request, response);
            }

            Slurp.logger.info("Registered SSE subscriptions: " + subscriptions);
        } catch (URISyntaxException e) {
            throw new ApiUrlException();
        } catch (IOException | InterruptedException e) {
            throw new CreateSessionException(request);
        }
    }

    public static CompletableFuture<SlurpSession> getSession(String shortcode) throws ApiResponseException,
            ApiUrlException, MissingSessionException {
        HttpRequest request = null;

        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/api/collections/sessions/records?skipTotal=true&filter=shortcode='"
                            + shortcode + "'"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + instance.token)
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiResponseException(request, response);
            }

            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonArray("items");

            if (items.size() == 0) {
                throw new MissingSessionException();
            }

            JsonElement sessionElement = items.get(0);

            ResponseSession responseSession = PocketBaseGson.getGson()
                    .fromJson(sessionElement, ResponseSession.class);

            SlurpSession session = responseSession.toSlurpSession();
            Slurp.logger.log(Level.INFO,
                    String.format("Created session %s, (%s)", session.getShortcode(), session.getId()));

            return CompletableFuture.completedFuture(session);
        } catch (URISyntaxException e) {
            throw new ApiUrlException();
        } catch (IOException | InterruptedException e) {
            throw new MissingSessionException();
        }
    }

}
