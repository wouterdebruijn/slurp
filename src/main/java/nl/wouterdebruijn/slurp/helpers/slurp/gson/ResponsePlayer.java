package nl.wouterdebruijn.slurp.helpers.slurp.gson;

import com.google.gson.Gson;
import nl.wouterdebruijn.slurp.helpers.SlurpConfig;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpPlayer;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpSession;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpSessionManager;
import nl.wouterdebruijn.slurp.helpers.slurp.exceptions.ApiResponseException;
import nl.wouterdebruijn.slurp.helpers.slurp.exceptions.MissingSessionException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;

public class ResponsePlayer {
    public static class Consumable {
        private int sips;
        private int shots;
    }

    private String uuid;
    private String username;
    private String session;
    private Date created;
    private Date updated;

    private Consumable taken;
    private Consumable remaining;
    private Consumable giveable;

    private static final String API_URL = SlurpConfig.ApiUrl();

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
                        .build();

                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new ApiResponseException(request, response);
                }

                ResponseSession responseSession = gson.fromJson(response.body(), ResponseSession.class);
                slurpSession = responseSession.toSlurpSession();
                SlurpSessionManager.addSession(slurpSession);
            } catch (Exception e) {
                throw new MissingSessionException();
            }
        }

        return new SlurpPlayer(uuid, slurpSession, username);
    }
}
