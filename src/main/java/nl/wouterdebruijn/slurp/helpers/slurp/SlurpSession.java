package nl.wouterdebruijn.slurp.helpers.slurp;

import com.google.gson.Gson;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helpers.SlurpConfig;
import nl.wouterdebruijn.slurp.helpers.slurp.exceptions.*;
import nl.wouterdebruijn.slurp.helpers.slurp.gson.ResponseSession;
import nl.wouterdebruijn.slurp.helpers.slurp.gson.ResponseSessionPlayers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.logging.Level;

public class SlurpSession {
    private final String shortcode;
    private final String uuid;
    private final boolean active;

    private static final String API_URL = SlurpConfig.ApiUrl();

    public SlurpSession(String shortcode, String uuid, boolean active) {
        this.shortcode = shortcode;
        this.uuid = uuid;
        this.active = active;
    }

    public static SlurpSession create() throws ApiException, ApiResponseException {
        Gson gson = new Gson();
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
            SlurpSession session = responseSession.toSlurpSession();
            Slurp.logger.log(Level.INFO, String.format("Created session %s, (%s)", session.getShortcode(), session.getUuid()));
            SlurpSessionManager.addSession(session);
            return session;
        } catch (URISyntaxException e) {
            throw new ApiUrlException();
        } catch (IOException | InterruptedException e) {
            throw new CreateSessionException(request);
        }
    }

    public ArrayList<SlurpPlayer> getPlayers() throws ApiUrlException, CreateSessionException, MissingSessionException, ApiResponseException {
        Gson gson = new Gson();
        HttpRequest request = null;

        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/session/entity/" + uuid))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiResponseException(request, response);
            }

            ResponseSessionPlayers sessionPlayersResponse = gson.fromJson(response.body(), ResponseSessionPlayers.class);
            return sessionPlayersResponse.toSlurpPlayers();
        } catch (URISyntaxException e) {
            throw new ApiUrlException();
        } catch (IOException | InterruptedException e) {
            throw new CreateSessionException(request);
        }
    }

    public String getUuid() {
        return uuid;
    }

    public String getShortcode() {
        return shortcode;
    }
}
