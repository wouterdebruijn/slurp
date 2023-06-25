package nl.wouterdebruijn.slurp.helper.game.entity;

import com.google.gson.Gson;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.api.ResponseEntry;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.manager.DrinkingBuddyManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class SlurpEntry {
    private static final String API_URL = SlurpConfig.apiUrl();
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

    public static CompletableFuture<SlurpEntry> createDirect(SlurpEntryBuilder entry, String token) {
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
                //throw new ApiResponseException(request, response);
                return CompletableFuture.failedFuture(new Exception("API exception"));
            }
            ResponseEntry responseEntry = gson.fromJson(response.body(), ResponseEntry.class);
            //callback.accept(responseEntry.toSlurpEntry());
            return CompletableFuture.completedFuture(responseEntry.toSlurpEntry());
        } catch (Exception e) {
            Slurp.logger.log(Level.SEVERE, "Error while creating entry", e);
            return CompletableFuture.failedFuture(new Exception("Error while creating entry"));
        }
    }

    public static CompletableFuture<ArrayList<SlurpEntry>> create(SlurpEntryBuilder entry, String token) {

        // Get drinking buddies for player
        SlurpPlayer player = SlurpPlayerManager.getPlayer(entry.getPlayerUuid());

        if (player == null) {
            Slurp.logger.log(Level.SEVERE, "Player not found");
            return CompletableFuture.failedFuture(new Exception("Player not found"));
        }

        // Get list of drinkingbuddies
        ArrayList<SlurpPlayer> buddies = DrinkingBuddyManager.getBuddiesOfPlayer(player);

        ArrayList<SlurpEntry> entries = new ArrayList<>();

        // Create entry for player
        try {
            entries.add(createDirect(entry, token).get());

            // If the player has buddies create entries for them too
            if (buddies != null && entry.shouldTransferToDrinkingBuddies()) {
                // Create entry for each buddy
                for (SlurpPlayer buddy : buddies) {
                    entries.add(createDirect(entry.copyForPlayer(buddy), token).get());
                }
            }
        } catch (InterruptedException|ExecutionException e) {
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(entries);
    }
}
