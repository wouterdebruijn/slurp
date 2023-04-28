package nl.wouterdebruijn.slurp.helper.game.entity;

import com.google.gson.Gson;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.exceptions.*;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.api.ResponseSession;
import nl.wouterdebruijn.slurp.helper.game.api.ResponseSessionPlayers;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpSessionManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.logging.Level;

public class SlurpSession {
    private static final String API_URL = SlurpConfig.apiUrl();
    private final String shortcode;
    private final String uuid;
    private final boolean active;
    private String token;

    public SlurpSession(SlurpSession session) {
        this.shortcode = session.getShortcode();
        this.uuid = session.getUuid();
        this.active = session.isActive();
        this.token = session.getToken();
    }

    public SlurpSession(String shortcode, String uuid, boolean active, String token) {
        this.shortcode = shortcode;
        this.uuid = uuid;
        this.active = active;
        this.token = token;
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
            Slurp.logger.info(gson.toJson(responseSession));

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

    public String getUuid() {
        return uuid;
    }

    public String getShortcode() {
        return shortcode;
    }

    public String getToken() {
        return token;
    }

    public void overwriteToken(String token) {
        this.token = token;
    }

    public boolean isActive() {
        return active;
    }
}
