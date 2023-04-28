package nl.wouterdebruijn.slurp.helper.game.api;

import com.google.gson.Gson;
import nl.wouterdebruijn.slurp.exceptions.ApiResponseException;
import nl.wouterdebruijn.slurp.exceptions.MissingSessionException;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpSessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;

public class ResponsePlayer {
    private static final String API_URL = SlurpConfig.apiUrl();
    private String uuid;
    private String username;
    private String session;
    private Date created;
    private Date updated;

    private Consumable taken;
    private Consumable remaining;
    private Consumable giveable;

    //    Authentication token for requests
    private String token;

    public SlurpPlayer toSlurpPlayer() throws MissingSessionException {
        SlurpSession slurpSession = SlurpSessionManager.getSession(session);

        HttpRequest request = null;
        Gson gson = new Gson();

        if (slurpSession == null) {
            try {
                request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL + "/session/entity/" + session))
                        .GET()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .build();

                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new ApiResponseException(request, response);
                }

                ResponseSession responseSession = gson.fromJson(response.body(), ResponseSession.class);
                slurpSession = responseSession.toSlurpSession();
            } catch (Exception e) {
                throw new MissingSessionException();
            }
        }

        SlurpSession sessionCopy = new SlurpSession(slurpSession);

        sessionCopy.overwriteToken(this.token);
        return new SlurpPlayer(uuid, sessionCopy, username);
    }

    public static class Consumable {
        private int sips;
        private int shots;
    }
}
