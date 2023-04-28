package nl.wouterdebruijn.slurp.helper.game.entity;

import com.google.gson.Gson;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.exceptions.ApiResponseException;
import nl.wouterdebruijn.slurp.helper.game.api.ResponseEntry;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;

public class SlurpEntry {
    private static final String API_URL = SlurpConfig.ApiUrl();
    private String uuid;
    private int sips;
    private int shots;
    private SlurpPlayer player;
    private boolean giveable;
    private boolean transfer;

    public SlurpEntry(String uuid, int sips, int shots, SlurpPlayer player, boolean giveable, boolean transfer) {
        this.uuid = uuid;
        this.sips = sips;
        this.shots = shots;
        this.player = player;
        this.giveable = giveable;
        this.transfer = transfer;
    }

    public static SlurpEntry create(SlurpEntryBuilder entry, String token) {
        try {
            Gson gson = new Gson();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/entry"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(entry)))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiResponseException(request, response);
            }

            ResponseEntry responseEntry = gson.fromJson(response.body(), ResponseEntry.class);
            return responseEntry.toSlurpEntry();
        } catch (Exception e) {
            Slurp.logger.log(Level.SEVERE, "Error while creating entry", e);
            return null;
        }
    }
}
